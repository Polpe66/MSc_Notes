# Slide 4: Indicizzazione di una Collezione di Documenti

L'indicizzazione è un processo fondamentale nell'Information Retrieval per organizzare ed estrarre informazioni da grandi collezioni di dati testuali in maniera efficiente. In questo capitolo verranno esplorati i fondamenti teorici e pratici dell'elaborazione delle query, partendo dall'architettura base del sistema fino all'analisi matematica del peso dei termini nei documenti.

### Architettura del Sistema di Retrieval

L'architettura di un moderno motore di ricerca si divide concettualmente in due fasi temporali distinte: la fase **Offline** e la fase **Online**. Questa netta separazione è essenziale perché l'indicizzazione richiede molto tempo e deve essere preparata prima che l'utente effettui una ricerca, che invece deve avvenire quasi istantaneamente.

Durante la fase offline, il sistema si basa su una **Document Collection** (collezione di documenti) per generare le strutture dati primarie. Attraverso il processo di **Indexing** si costruisce l'**Inverted Index** (indice invertito), che mapperà i futuri termini ricercati direttamente ai documenti in cui compaiono. Parallelamente, un modulo chiamato **Feature Processor** estrae caratteristiche aggiuntive dei documenti e le salva all'interno del **Document Features Repository**. Infine, utilizzando un insieme di **Training Data**, la fase di **Training** provvede ad addestrare un modello algoritmico (**Learning-to-rank Model**) che sarà poi usato per l'ordinamento dei risultati.

Nel momento in cui inizia la fase online, l'utente inserisce una **Query**. Questa stringa viene processata dal modulo di **Expanded Query** per arricchirla di termini semanticamente utili, per poi passare al vero e proprio **Query Processing**. Questo componente interroga l'Inverted Index creato precedentemente. I risultati preliminari vengono poi uniti alle caratteristiche estratte durante il passaggio di **Feature Lookup and Computation**. A questo punto, il sistema sfrutta la **Learned Ranking Function** per riordinare i documenti in base alla loro rilevanza e mostrarli all'utente, ad esempio sulla tipica interfaccia di Google.

![[Pasted image 20260414115557.png]]

### Modelli di Elaborazione delle Query

Per capire quali documenti recuperare da una collezione, esistono approcci teorici differenti. Il passaggio fondamentale, descritto anche come Term Matching, controlla la presenza e la frequenza delle parole della query nei documenti esplorati.

Il **Boolean model of information retrieval (BIR)** è il modello di IR più classico. In questo paradigma, i documenti non sono altro che insiemi di termini matematicamente intesi. Il recupero avviene verificando rigorosamente se i documenti contengano o meno le parole ricercate in base alle rigide condizioni booleane dettate dalla query. Tra gli esempi più tipici troviamo:

- `cat AND dog`: permette di trovare tutti i documenti che contengono contemporaneamente entrambi i termini.

- `cat OR dog`: permette di trovare tutti i documenti in cui sia presente almeno uno dei due termini.

- `(cat OR dog) AND NOT marmot`: combina gli operatori per risultati logici più complessi escludendo la parola "marmot".

Questo modello si dimostra ottimo per utenti molto esperti che hanno una comprensione esatta dei propri bisogni e di come è composta la collezione. Tuttavia, formulare una stringa booleana perfetta per ottenere un numero ragionevole di risultati richiede notevole abilità. Senza di essa, le ricerche tendono a fallire miseramente: l'operatore AND si rivela spesso troppo restrittivo (restituendo 0 o troppi pochi documenti), mentre l'operatore OR allarga in modo esagerato lo spettro restituendo migliaia di corrispondenze ingestibili.

A fronte delle limitazioni del modello booleano puro, il paradigma del **Ranked Retrieval** cambia radicalmente l'approccio. Invece di limitarsi a identificare un set di documenti che soddisfano matematicamente un'espressione logica, il sistema attribuisce loro uno score e riordina i migliori documenti (top) all'interno della collezione per una query specifica. Il principio cardine del Ranked Retrieval è che l'utente desidera ricevere i risultati "in ordine", dando un'immediata priorità ai documenti più utili a soddisfare il proprio bisogno di informazioni.

### Il Modello Bag of Words (BOW)

Per poter effettuare questo ordinamento, è necessario sapere quanto un termine "pesi" nel testo, ad esempio analizzando il numero di occorrenze di una specifica parola all'interno di un documento. A questo scopo si utilizza il modello **Bag of Words**, in cui ogni documento viene convertito in un vettore di conteggio matematico all'interno di uno spazio numerico multidimensionale $\mathbb{N}^v$.

Come suggerisce il nome, ogni documento è interpretato come un sacchetto di parole ("bag of words") privo di sintassi, il cui unico valore analizzato è la frequenza testuale. Di seguito, una tabella che mostra questo paradigma applicato all'occorrenza di alcune parole chiave (Antony, Brutus, Caesar, Calpurnia, Cleopatra, mercy, worser) nelle opere teatrali di Shakespeare:

| **Termine** | **Julius Caesar** | **The Tempest** | **Hamlet** | **Othello** | **Macbeth** | **Antony and Cleopatra** |
| ----------- | ----------------- | --------------- | ---------- | ----------- | ----------- | ------------------------ |
| Antony      | 73                | 0               | 0          | 0           | 0           | 157                      |
| Brutus      | 157               | 0               | 1          | 0           | 0           | 4                        |
| Caesar      | 227               | 0               | 2          | 1           | 1           | 232                      |
| Calpurnia   | 10                | 0               | 0          | 0           | 0           | 0                        |
| Cleopatra   | 0                 | 0               | 0          | 0           | 0           | 57                       |
| mercy       | 0                 | 3               | 5          | 5           | 1           | 2                        |
| worser      | 0                 | 1               | 1          | 1           | 0           | 2                        |

### La Ponderazione: Term Frequency e Document Frequency

La **Term Frequency (tf)**, indicata con $tf_{t,d}$, rappresenta esattamente questo valore: la frequenza (ovvero il conteggio totale o il numero di volte) in cui uno specifico termine $t$ si manifesta nel documento $d$. Sebbene istintivamente verrebbe voglia di usare la $tf_{t,d}$ nuda e cruda per calcolare il match score tra query e documento, la frequenza grezza (raw term frequency) non produce risultati adeguati. Sebbene sia vero che un documento in cui una parola occorre 10 volte possa essere percepito come più rilevante rispetto a un testo in cui occorre 1 sola volta, non possiamo sostenere che sia "10 volte più rilevante". In sintesi, la rilevanza per l'utente non scala mai in maniera proporzionale rispetto alla semplice frequenza di un termine.

Bisogna prendere in considerazione un altro fattore vitale: la **Document frequency (df)**. A livello probabilistico, i termini maggiormente frequenti risultano molto meno informativi rispetto a parole più rare. Se si considera un termine di ricerca estremamente comune nella lingua e nella collezione (ad esempio "red", "high", "increase", "line"), un documento che lo contiene ha una buona probabilità di essere rilevante a priori rispetto a uno che non lo cita. Purtroppo, trattandosi di un termine diffusissimo, il segnale non è forte e non descrive precisamente l'intento dell'utente, in quanto gran parte dei documenti all'interno della medesima collezione vanterà la presenza di tale termine della query. Di conseguenza, l'algoritmo mira ad assegnare pesi più bassi per termini più rari come "high".

### Inverse Document Frequency (idf)

La soluzione che combina la necessità di mappare i termini e valutarne l'impatto isolato è fornita dalla metrica dell'**Inverse document frequency (idf)**. Si tratta di una misura precisa di "quante informazioni" un termine è in grado di fornire alla query dell'utente, quantificando se si tratta di una stringa troppo comune o opportunamente rara all'interno di tutti i documenti esplorabili.

Da un punto di vista matematico, l'idf è definita come la frazione inversa dei documenti contenenti quel termine, elaborata però tramite una scala logaritmica. La formula di calcolo è strutturata nel modo seguente:

$$idf(t,D)=\log\frac{N}{|\{d:d\in D\text{ and }t\in d\}|}$$

Le componenti di questa equazione sono:

- **$D$**: Rappresenta l'insieme globale di tutti i documenti presenti nel corpus.

- **$N$**: Corrisponde al numero totale dei documenti presenti nel corpus, esprimibile come $N=|D|$.

- **$n_{t}$**: Rappresenta il denominatore $|\{d\in D:t\in d\}|$, ossia l'esatto numero dei documenti in cui compare almeno una volta la parola cercata $t$ (si esprime con $tf(t,d)\neq0$).

Questa operazione di divisione nasconde però un'insidia di calcolo: se un utente dovesse cercare un termine totalmente inesistente nel corpus di documenti, il conteggio al denominatore diverrebbe 0, scatenando un errore matematico fatale (la divisione per zero). Per scongiurare l'interruzione della pipeline, la pratica informatica raccomanda di apportare un aggiustamento algoritmico aggiungendo la costante 1. Il numeratore diventerà $1+N$ e il denominatore si correggerà diventando $1+|\{d\in D:t\in d\}|$.

---

### Concetti Chiave

- **Boolean Retrieval e Ranked Retrieval**: I due poli dell'Information Retrieval: il primo valuta matematicamente set logici e non fornisce un ordine, il secondo genera liste riordinate a priorità d'uso e utilità informativa.

- **Bag of Words (BOW)**: Modello strutturale che scardina la grammatica di un testo per tradurlo in vettori numerici descrivendo puramente la mole di presenza delle singole parole in un array multidimensionale.

- **TF-IDF**: Sistema di pesatura combinato che non si affida ciecamente alla sola ripetizione di un termine (Term Frequency), ma ne modula la rilevanza logaritmica tenendo conto di quanto quella parola è abusata nel corpus generico (Inverse Document Frequency).

---

### I Documenti come Vettori e lo Spazio Vettoriale

Per calcolare quanto un documento sia rilevante rispetto a una determinata ricerca, i moderni sistemi di elaborazione utilizzano approcci matematici che trattano i testi come entità geometriche. Nel modello TF-IDF, lo score di un documento $D$ rispetto a una query $q$ viene calcolato sommando i prodotti tra l'Inverse Document Frequency (IDF) e la Term Frequency (tf) per ogni termine della query. Questa operazione è descritta dalla formula matematica $score(q,D)=\sum_{i=1}^{n}IDF(q_{i})\cdot tf(q_{i},D)$.

Questo approccio trasforma di fatto l'insieme dei documenti in uno spazio vettoriale a $|V|$ dimensioni, dove la variabile $|V|$ rappresenta la grandezza dell'intero vocabolario a disposizione. In questo spazio geometrico, i termini del vocabolario fungono da assi cartesiani , mentre i documenti si posizionano al suo interno sotto forma di punti o vettori. Questo spazio diventa rapidamente iper-dimensionale: quando si applica tale modello a un motore di ricerca web reale, le dimensioni raggiungono agilmente le decine di milioni.

### La Valutazione della Query e le Inefficienze della Matrice

![[Pasted image 20260414120107.png]]

Dal punto di vista concettuale, la valutazione di una query in questo spazio vettoriale equivarrebbe a moltiplicare il vettore della query per l'intera matrice Termini-Documenti. Tuttavia, operare direttamente su questa matrice comporta due gravissimi problemi di ottimizzazione. In primo luogo, c'è una forte inefficienza spaziale: la matrice risultante è gigantesca ma estremamente sparsa, ovvero composta prevalentemente da zeri, sprecando enormi quantità di memoria. In secondo luogo, c'è un'altrettanto grave inefficienza temporale: per calcolare i risultati bisognerebbe processare l'intera collezione di documenti in tempo reale (online) durante la ricerca dell'utente, un'operazione insostenibile per un sistema reattivo.

### L'Indice Invertito per il Boolean Retrieval

Per superare i limiti di calcolo della matrice, i motori di ricerca si affidano all'**Inverted Index** (Indice Invertito). Si tratta di una struttura dati che memorizza una mappatura diretta che va dai termini alle loro rispettive occorrenze all'interno dell'insieme di documenti. Questo strumento rappresenta il componente centrale di qualsiasi algoritmo di indicizzazione in un motore di ricerca tipico. L'obiettivo primario dell'indice invertito è ottimizzare al massimo la velocità della query, permettendo di trovare istantaneamente i documenti in cui compare un set specifico di parole.
![[Pasted image 20260414120640.png]]



Architetturalmente, l'indice invertito è composto da due parti. Da un lato c'è il **Dictionary** (Dizionario), che contiene l'elenco dei termini estratti dalla collezione. Dall'altro lato ci sono le **Posting Lists** (Liste di Registrazione), che per ogni termine del dizionario contengono semplicemente gli identificativi (ID) dei documenti in cui quel termine è presente. Ad esempio, un termine comune come "a" punterà a una Posting List molto lunga (es. documenti 1, 2, 3), mentre parole più specifiche avranno liste più corte.

### Adattare l'Indice Invertito per Modelli Avanzati (BM25)

Mentre la struttura di base con soli ID dei documenti è sufficiente per il Boolean Retrieval puro, modelli di ranking più sofisticati necessitano di ulteriori dati quantitativi. Quando si costruiscono indici invertiti per algoritmi avanzati come il **BM25**, la struttura si arricchisce in modo significativo.

![[Pasted image 20260414120711.png]]

Nel Dictionary vengono salvate due nuove metriche per ogni termine. La prima è la $df_t$ (Document Frequency), che indica esattamente il numero totale di documenti che contengono almeno una singola occorrenza del termine $t$ . La seconda metrica è la $F_t$ (Collection Frequency), che esprime il numero totale assoluto di occorrenze di quel termine nell'intera collezione di testi. Di conseguenza, anche le Posting Lists si evolvono: invece di contenere solo l'ID del documento, ora memorizzano delle coppie di valori che includono sia l'identificativo del documento sia il conteggio locale del termine in quello specifico testo .

---

### Concetti Chiave

- **Spazio Vettoriale**: Un modello geometrico in cui i termini del vocabolario fungono da assi e i documenti sono rappresentati come vettori multidimensionali, permettendo il calcolo del punteggio di rilevanza tramite formule come il TF-IDF.

- **Inverted Index (Indice Invertito)**: La struttura dati fondamentale dei motori di ricerca, creata per superare le inefficienze della scansione lineare. Funziona mappando direttamente un termine verso l'elenco dei documenti che lo contengono.

- **Dictionary e Posting Lists**: Le due anime dell'indice invertito. Il Dictionary raccoglie le parole chiave e le loro frequenze globali ($df_t$ e $F_t$), mentre le Posting Lists fungono da registri che collegano il termine agli ID dei documenti rilevanti.

---

### Elaborazione della Query e Ricerca Avanzata

Il processo di **Query Processing** ha il compito fondamentale di tradurre la richiesta dell'utente in operazioni algoritmiche sui documenti. Un esempio pratico per visualizzare questa interazione è la Ricerca Avanzata di Google, un'interfaccia che permette di inserire parole chiave in campi specifici applicando filtri binari per ottenere risultati estremamente mirati . Dal punto di vista della logica del sistema, le opzioni offerte all'utente si traducono in operatori precisi. Richiedere la presenza di "tutte queste parole" equivale a un operatore **Conjunctive AND**, mentre cercare "questa esatta parola o frase" attiva la **Phrase Search**, indicata tipicamente racchiudendo il testo tra virgolette .

Inoltre, il campo che permette di trovare "una qualunque di queste parole" innesca un operatore **Disjunctive OR** (scrivendo OR tra i termini), e la richiesta di non includere "nessuna di queste parole" corrisponde all'operatore **NOT**, azionabile inserendo un segno meno prima del termine indesiderato . Infine, le interfacce avanzate consentono l'inserimento di range numerici, come pesi, prezzi o date, semplicemente posizionando due punti consecutivi tra i valori limite desiderati.

### Strategie di Scorrimento: TAAT e DAAT

Una volta che la query è stata interpretata, il sistema deve interrogare l'Inverted Index. Per elaborare i termini e scorrere le relative **Posting Lists**, i motori di ricerca utilizzano due metodologie principali: l'approccio Term-at-a-time e l'approccio Document-at-a-time.

La strategia **Term-at-a-time (TAAT)**, o elaborazione termine per termine, processa la query leggendo e analizzando sequenzialmente un'intera Posting List alla volta per ogni parola cercata . Per tenere traccia progressiva dei punteggi, questo metodo crea e mantiene in memoria una struttura chiamata **accumulatore** per ogni documento rilevato durante la scansione. Man mano che il sistema macina i documenti associati al termine corrente, aggiorna dinamicamente lo score (ovvero il valore di rilevanza) all'interno del rispettivo accumulatore .

Al contrario, la strategia **Document-at-a-time (DAAT)** si basa su una scansione simultanea e in parallelo delle Posting Lists di tutti i termini che compongono la query . In questo paradigma, il sistema fa avanzare molteplici liste temporaneamente, calcolando lo score definitivo di un documento nel momento esatto in cui questo viene intercettato in una o più liste incrociate . Grazie a questa sincronia, l'approccio DAAT non richiede l'utilizzo di accumulatori globali da aggiornare a posteriori, ma gestisce e aggiorna direttamente una lista già ordinata dei risultati finali.

### Il Funzionamento Pratico del Term-at-a-time (TAAT)

![[Pasted image 20260414120941.png|697]]

![[Pasted image 20260414121032.png]]

Per comprendere nel dettaglio l'esecuzione pratica del TAAT, possiamo osservare la valutazione di una query composta da due parole chiave, ad esempio "information" e "retrieval" . Il sistema inizia il suo compito concentrandosi in via esclusiva sulla Posting List del primo termine, "information" . Non appena intercetta il primo documento della lista, l'algoritmo inizializza un **accumulatore**, il quale è strutturato per conservare una coppia di dati fondamentali: l'indice univoco del documento (DocId) e il numero di parole chiave della query finora ritrovate, che funge da punteggio temporaneo .

Procedendo nella scansione lineare del primo termine, il sistema genera un nuovo accumulatore con score pari a 1 per ogni nuovo identificativo incontrato lungo la lista . Una volta esaurita completamente la lista di "information", il motore di ricerca si sposta sul secondo termine, "retrieval", e inizia a scorrerne la rispettiva Posting List . Quando, durante questa seconda passata, il sistema incontra un identificativo di documento già visitato in precedenza (e che quindi possiede già un suo accumulatore attivo), non crea una nuova voce, bensì individua l'accumulatore esistente e ne incrementa il punteggio, passandolo da 1 a 2 . Questo processo sequenziale di lettura e aggiornamento prosegue ininterrottamente, accumulando i valori finché la lettura del secondo termine non giunge al termine .


---

### Concetti Chiave

- **Conjunctive AND / Disjunctive OR**: Operatori di ricerca booleani essenziali; l'AND impone la presenza simultanea di tutti i termini, mentre l'OR ne richiede almeno uno, permettendo al Query Processing di filtrare con precisione le richieste utente.

- **Term-at-a-time (TAAT)**: Strategia di elaborazione dell'Inverted Index che processa le liste di documenti una parola alla volta, utilizzando spazi di memoria temporanei per sommare gradualmente i punteggi.

- **Document-at-a-time (DAAT)**: Strategia di scorrimento parallela che valuta tutti i termini della query in contemporanea, calcolando istantaneamente la rilevanza dei documenti e mantenendo nativamente una lista di risultati già ordinata.

- **Accumulatore**: Struttura dati fondamentale nell'approccio TAAT, adibita a memorizzare l'identificativo di un documento (DocId) associato al suo punteggio temporaneo in fase di elaborazione della query.

---

### La Gestione degli Accumulatori e le Strutture Dati nel TAAT

Continuando l'esplorazione della strategia Term-at-a-time (TAAT), sorge un problema tecnico di non poco conto: come fa il sistema a ritrovare e aggiornare rapidamente il punteggio corretto per ogni singolo documento man mano che legge le varie liste? Per risolvere questo ostacolo, l'architettura implementa una **Direct Access Table** (Tabella ad Accesso Diretto) oppure una **Hash Table** (Tabella Hash) . Queste strutture dati permettono di collegare in modo istantaneo l'identificativo del documento al suo rispettivo accumulatore, abbattendo i tempi di ricerca interni al sistema .
![[Pasted image 20260414121552.png]]

Attraverso l'uso di queste tabelle, il TAAT è in grado di supportare la complessa logica delle query. L'algoritmo può tracciare in memoria la presenza condivisa dei termini per risolvere le intersezioni logiche richieste dall'operatore AND, oppure unire i risultati espandendoli per l'operatore OR . Allo stesso modo, questa memoria temporanea viene interrogata per escludere specifici documenti in presenza di un operatore NOT, o ancora per sommare progressivamente pesi matematici molto più complessi qualora si utilizzi un modello di ranking avanzato come il BM25.

![[Pasted image 20260414121618.png]]
### Pro e Contro del Modello Term-at-a-time

L'approccio TAAT offre dei vantaggi e degli svantaggi ben precisi. Tra i suoi pro principali vi è sicuramente un'estrema facilità di implementazione a livello algoritmico . Al sistema basta infatti utilizzare una semplice operazione di base, la funzione `next()`, per scorrere linearmente e senza interruzioni la Posting List fino alla sua conclusione.

Tuttavia, i difetti di questa tecnica si fanno sentire pesantemente sulle performance dell'hardware e sull'efficienza di ricerca. Il contro più impattante è l'enorme quantità di "cache misses" (mancanze nella cache) causata dal vasto numero di accumulatori in uso. Poiché l'algoritmo deve aggiornare punteggi sparsi saltando continuamente da un punto all'altro della memoria, il processore fatica a mantenere i dati pronti all'uso. Inoltre, emerge un limite logico critico: il TAAT non offre alcuna possibilità di saltare ("skipping") gli identificativi dei documenti (DocIds) nel caso di query altamente selettive, come quelle che utilizzano l'operatore AND. L'algoritmo è costretto a leggere e processare tutto, perdendo tempo su documenti che potrebbero essere scartati a priori.


#QUAAAAAAAAA
### Il Modello DAAT e la Risoluzione dell'Operatore OR

Proprio per sopperire all'impossibilità di saltare i documenti inutili, entra in gioco il modello Document-at-a-time (DAAT), il quale analizza le Posting Lists procedendo in parallelo . Quando un utente richiede una query basata sull'operatore OR (ad esempio, cercando "information" OR "retrieval"), il DAAT deve fondamentalmente unire due o più liste. Per compiere questa unione in modo ottimale, il sistema sfrutta lo stesso algoritmo di fusione (merge algorithm) alla base del celebre ordinamento MergeSort .

[INSERIRE IMMAGINE: Diagramma dell'operazione di DAAT OR query, in cui i puntatori scorrono simultaneamente le Posting Lists di "information" e "retrieval" per estrarre e unire i documenti in una lista dei risultati.]

Con questa tecnica, il sistema ispeziona contemporaneamente i puntatori in testa a tutte le liste coinvolte, prelevando di volta in volta l'identificativo numericamente più piccolo . Questo procedimento restituisce direttamente una lista dei risultati perfettamente ordinata contenente i documenti in cui è presente almeno una delle parole chiave . Questo approccio porta con sé un beneficio enorme: possedere una lista ordinata nativamente permette di saltare in blocco interi gruppi di DocIds quando si affrontano query molto selettive, risparmiando preziose risorse di calcolo. Di contro, la struttura in parallelo richiesta dal DAAT risulta notevolmente più complessa da implementare, e la necessità di applicare algoritmi di ordinamento continuo porta a un costo computazionale iniziale più elevato per la pura creazione della lista dei risultati .

---

### Concetti Chiave

- **Direct Access Table / Hash Table**: Strutture dati impiegate nell'approccio TAAT per mappare velocemente l'ID di un documento al proprio accumulatore temporaneo, garantendo aggiornamenti rapidi per operatori logici o funzioni di calcolo avanzate.

- **Cache Miss**: Un problema di efficienza hardware tipico del TAAT, in cui i continui salti di memoria causati dall'elevato numero di accumulatori impediscono al processore di ottimizzare le letture veloci, rallentando le performance.

- **Algoritmo MergeSort**: Un principio di unione ordinata preso in prestito dal DAAT per risolvere le query con operatore OR. Fonde in tempo reale le Posting Lists estraendo l'elemento minore, producendo un elenco combinato e perfettamente indicizzato.

- **Skipping dei DocIds**: La capacità (assente nel TAAT, ma possibile nel DAAT grazie alle liste già ordinate) di ignorare strategicamente enormi blocchi di documenti irrilevanti per risparmiare tempo nelle ricerche molto selettive.

---

### L'Esecuzione Pratica del DAAT per le Query OR

Per comprendere a fondo il funzionamento dell'approccio **Document-at-a-time (DAAT)** di fronte a una query disgiuntiva (operatore **OR**), è utile analizzare l'esecuzione pratica dell'algoritmo di unione basato sul **MergeSort** . Immaginiamo di dover processare due **Posting Lists** parallele: la lista per il termine "information", contenente i documenti 1, 5, 8, 11, 13, 20, 35, 40, 42, e la lista per il termine "retrieval", contenente i documenti 1, 5, 6, 8, 11, 15, 17, 50, 60 .

[INSERIRE IMMAGINE: Scorrimento parallelo dei puntatori nelle liste "information" e "retrieval" per formare l'unione dei risultati in una query OR procedendo per ordine numerico crescente]

Il sistema avvia l'ispezione posizionando un puntatore all'inizio di entrambe le liste. Entrambi i puntatori indicano il documento 1, di conseguenza questo valore viene aggiunto ai risultati . Facendo avanzare entrambi i cursori, si riscontra un'altra corrispondenza sul documento 5, che viene a sua volta inglobato nell'elenco finale . Il comportamento dell'algoritmo cambia quando i valori divergono: il puntatore della lista "information" si ferma sul documento 8, mentre quello di "retrieval" indica il documento 6 . In questo scenario, il sistema seleziona l'identificativo numericamente più piccolo, aggiungendo il 6 ai risultati e facendo avanzare unicamente il puntatore della lista "retrieval" .

Questo processo di avanzamento asimmetrico e inserimento ordinato continua ininterrottamente fino all'esaurimento di entrambe le liste. Il risultato finale è un insieme di documenti perfettamente ordinato: 1, 5, 6, 8, 11, 13, 15, 17, 20, 35, 40, 50, 60 . Tuttavia, questa logica di unione totale porta a una conclusione algoritmica inevitabile: **la query OR è estremamente costosa (expensive)**. Questo accade perché, per generare la lista completa, il sistema è letteralmente costretto a "toccare" e valutare singolarmente tutte le registrazioni (postings) presenti nelle liste analizzate, senza poterne scartare nessuna a priori.

### La Selettività delle Query AND e l'Intersezione

Il comportamento del motore di ricerca cambia radicalmente quando l'utente richiede l'uso di un operatore **AND** sempre all'interno del paradigma DAAT. Invece di unire i dati, l'operatore congiuntivo impone una rigida **intersezione delle posting lists** (Intersection of posting lists).

[INSERIRE IMMAGINE: Scorrimento dei puntatori per l'intersezione logica delle liste "information" e "retrieval" in una query AND, dove avanzano solo in caso di discrepanza]

Riprendendo in esame le medesime liste ("information" e "retrieval"), il sistema inizia la scansione parallelamente dal documento 1 . Essendo l'identificativo presente in ambedue i registri, la condizione logica è soddisfatta e il documento diventa un candidato valido per i risultati. Entrambi i puntatori avanzano quindi al documento 5, confermando un'altra corrispondenza .

L'efficienza dell'intersezione si palesa quando si presenta una discrepanza numerica. Quando il puntatore superiore indica l'8 e quello inferiore indica il 6, il sistema comprende istantaneamente che il documento 6 non possiede entrambi i termini richiesti. Di conseguenza, il motore scarta il documento 6 e fa avanzare esclusivamente il puntatore che si trova sul valore inferiore, portandolo a pareggiare o superare l'altro cursore per cercare una nuova corrispondenza. Questa metodica garantisce una forte selettività e impedisce l'aggiunta di documenti irrilevanti alla lista finale.

---

### Concetti Chiave

- **DAAT OR Query (Unione)**: Procedimento basato sull'algoritmo di unione del MergeSort che processa le liste estraendo progressivamente il DocId più piccolo. È un'operazione computazionalmente molto dispendiosa perché impone la lettura obbligatoria di ogni singolo elemento di tutte le liste.

- **DAAT AND Query (Intersezione)**: Strategia altamente selettiva che filtra i documenti. L'algoritmo convalida un DocId solo se i puntatori di tutte le liste analizzate si allineano simultaneamente sul medesimo valore, scartando i restanti.

- **Costo Computazionale**: Il principio secondo cui l'operatore OR sovraccarica il sistema ("touch all the postings"), mentre l'operatore AND permette di sfruttare le discrepanze numeriche per far avanzare i cursori più rapidamente, ignorando i documenti che non soddisfano i requisiti di intersezione.

---

## Ottimizzazione dell'Intersezione e Gestione dei Puntatori

Questa sezione esplora come i motori di ricerca ottimizzano le query restrittive (quelle basate sull'operatore logico AND) all'interno dell'approccio Document-at-a-time (DAAT), introducendo comandi specifici per far "saltare" i puntatori e accelerare l'elaborazione scartando enormi blocchi di dati inutili.

### L'Algoritmo di Intersezione Lineare

Come anticipato, la valutazione di una query AND nel modello DAAT si traduce operativamente nell'**intersezione delle posting lists**. A livello di codice, questo procedimento viene gestito da una funzione dedicata (spesso descritta come `intersect(p1, p2)`) che inizializza due puntatori all'inizio delle rispettive liste . L'algoritmo scorre le liste in un ciclo continuo, confrontando i valori presenti nelle celle correnti . Se l'identificativo del documento corrisponde in entrambe le liste, questo viene aggiunto ai risultati finali e ambedue i puntatori avanzano di un singolo passo ($i+=1$; $j+=1$) . Nel caso in cui vi sia una discrepanza numerica, il sistema si limita a far avanzare di un solo passo il puntatore associato al valore più piccolo, nel tentativo di riallinearlo con la lista opposta .

[INSERIRE IMMAGINE: Pseudocodice dell'algoritmo di intersezione lineare (intersect) che mostra il ciclo while e l'avanzamento unitario dei puntatori i e j]

### Comandi e Operazioni di Base sulle Posting Lists

Per permettere al sistema di interagire fisicamente con questi registri, è stato definito un set di operazioni di base che comandano lo spostamento del puntatore all'interno della **Posting List** .

- Il comando **$first_t()$** inizializza la ricerca posizionando il puntatore esattamente sul primo identificativo di documento (DocId) disponibile nella lista.

- Per procedere nella lettura sequenziale, si invoca l'operazione **$next_t()$**, che fa semplicemente scivolare il cursore nella casella adiacente successiva.

- L'operazione più complessa è il **$nextGEQ_t(d)$** (Next Greater or Equal): questo comando ordina al puntatore di scavalcare i dati e fermarsi sul primo DocId che risulti essere strettamente maggiore o uguale al parametro $d$ fornito.

Inoltre, per estrapolare i dati effettivi durante queste manovre, il motore utilizza le funzioni **$docId_t()$**, che restituisce l'ID del documento attualmente puntato, e **$position_t()$**, che restituisce l'indice numerico di posizione del puntatore all'interno dell'array .

[INSERIRE IMMAGINE: Esempio visivo dello stato di un puntatore su una posting list dopo l'esecuzione dei comandi first() e next(), con l'aggiornamento dei valori docId e position]

### Implementare i Salti: Ricerca Binaria vs Skip Pointers

L'operazione **$nextGEQ$** è il vero motore dell'ottimizzazione, ma la sua implementazione pratica pone delle sfide . Un primo approccio puramente matematico si affida alla **Ricerca Binaria (o Esponenziale)**. Sebbene questa tecnica garantisca un tempo di esecuzione teorico pari a $\Theta(\log(n/t))$ per eseguire una serie di salti, nella pratica informatica si rivela inefficace a causa della grandezza imprevedibile dei salti e, soprattutto, risulta incompatibile con la maggior parte degli algoritmi di compressione che non supportano l'accesso casuale in memoria .

La soluzione adottata in ambito ingegneristico è l'uso degli **Skip Pointers** (Puntatori di Salto). Questi costituiscono delle vere e proprie "scorciatoie" fisiche inserite a intervalli regolari all'interno della lista, progettate per far saltare al cursore un numero prefissato $k$ di elementi. Questo approccio si sposa perfettamente con le necessità di stoccaggio moderne: permette infatti di comprimere interi blocchi di dati, applicando ad esempio la codifica di compressione **Elias-Fano** direttamente ai valori dei salti per minimizzare lo spazio occupato su disco .

[INSERIRE IMMAGINE: Diagramma di una posting list dotata di Skip Pointers, rappresentati come archi rossi che collegano elementi distanti per permettere salti diretti bypassando blocchi di documenti]

### L'Intersezione DAAT Ottimizzata con nextGEQ

Integrando il comando di salto rapido all'interno della logica DAAT, l'algoritmo di elaborazione della query AND subisce un'evoluzione drastica (identificata spesso dalla funzione `intersect_nextGEQ(p1, p2)`). La logica di base rimane l'intersezione, ma cambia la reazione del sistema in caso di mancata corrispondenza: invece di far avanzare il cursore più arretrato di una sola casella ($i+=1$), l'algoritmo lo costringe a compiere un salto dinamico calcolato.

Se il valore nella lista 1 è inferiore a quello della lista 2 ($p1[i] < p2[j]$), il sistema invoca direttamente il comando **$nextGEQ$** sulla prima lista, imponendo al puntatore di raggiungere istantaneamente un documento maggiore o uguale all'identificativo trovato nella seconda lista ($i = nextGEQ(p1, p2[j])$) . In questo modo, l'algoritmo aggira completamente la lettura, il confronto e la valutazione di decine o centinaia di documenti intermedi non rilevanti, restituendo la lista dei risultati in tempi enormemente più brevi.

[RIFERIMENTO VISIVO DEL PROFESSORE: Sequenza di scorrimento logico in cui un puntatore fermo sul documento 1 usa il comando nextGEQ per schizzare direttamente verso il documento 5 o l'8, riallineandosi con la seconda lista senza valutare i documenti in mezzo.]

---

### Concetti Chiave

- **Comandi Operativi ($first$, $next$, $docId$, $position$)**: L'interfaccia standard che permette all'algoritmo di manipolare e leggere la singola Posting List, estraendo valori o procedendo linearmente un elemento alla volta.

- **$nextGEQ(d)$ (Next Greater or Equal)**: L'operatore fondamentale per l'ottimizzazione del DAAT, che muove il cursore direttamente sul primo documento numericamente uguale o superiore al bersaglio desiderato, abilitando il salto di interi segmenti di memoria.

- **Skip Pointers**: Strutture di appoggio inserite nella lista per facilitare salti ampi in memoria (in contrapposizione all'inefficiente Ricerca Binaria), risultando essenziali per supportare la compressione avanzata dei dati come il metodo Elias-Fano.

- **Intersezione Ottimizzata**: La tecnica che, mediante l'uso di $nextGEQ$, migliora l'algoritmo AND base del DAAT permettendo ai puntatori di ignorare grandi quantità di documenti non allineati senza doverli scorrere uno a uno.

---

## Elaborazione delle Query e Architettura dei Sistemi di Information Retrieval

Il presente capitolo affronta le metodologie di elaborazione delle query all'interno dei sistemi di recupero dell'informazione. Esploreremo nel dettaglio le tecniche logiche di intersezione delle liste di posting, la gestione complessa delle query testuali esatte e l'architettura complessiva a strati che gestisce e classifica i documenti all'interno di un moderno motore di ricerca.

### DAAT: Query AND e la funzione nextGEQ

L'elaborazione di una query testuale che impone la presenza simultanea dei termini, ovvero di tipo **AND**, calcolata secondo l'approccio **DAAT** (Document-At-A-Time), sfrutta la navigazione parallela delle liste invertite dei termini. Prendendo come caso di studio l'intersezione delle liste associate alle parole "information" e "retrieval", analizziamo i dati in ingresso. La posting list del termine "information" comprende gli identificativi di documento 1, 5, 8, 11, 13, 20, 35, 40 e 42. Contestualmente, la lista per il termine "retrieval" include i documenti 1, 5, 6, 8, 11, 15, 17, 50 e 60.

[INSERIRE IMMAGINE: Scorrimento parallelo e intersezione delle due posting list "information" e "retrieval", con frecce che evidenziano i riscontri coincidenti sui documenti 5 e 8]

Per individuare i documenti che soddisfano entrambi i termini, l'algoritmo ricorre alla funzione descritta nello pseudocodice `intersect_nextGEQ(p1, p2)`. Il processo inizia azzerando i cursori di scorrimento `i` e `j` e inizializzando una lista vuota `r` destinata a contenere l'output finale. Il cuore dell'operazione è un ciclo `while` che continua a operare finché entrambi gli indici non eccedono le dimensioni delle rispettive liste `p1` e `p2`. Nel momento in cui il documento puntato dal primo cursore coincide con quello del secondo cursore (`p1[i] == p2[j]`), l'identificativo viene aggiunto alla lista dei risultati `r`, e di conseguenza ambedue i cursori vengono incrementati di uno prima di ricominciare l'iterazione. Qualora si presenti una discrepanza, per cui l'elemento nella prima lista risulti minore del corrispettivo nella seconda (`p1[i] < p2[j]`), il sistema non avanza linearmente ma esegue un salto calcolato riassegnando l'indice `i` tramite l'operatore **nextGEQ** (Greater or EQual), ricercando nella lista `p1` il primo valore maggiore o uguale all'elemento in `p2[j]`. All'opposto, se il valore in `p1` è maggiore, è l'indice `j` a compiere il salto adoperando la funzione `nextGEQ(p2, p1[i])`. Concluso l'esame incrociato, la funzione restituisce i documenti comuni raccolti in `r`.

### TAAT: Query AND con nextGEQ

Un differente paradigma di calcolo per le query testuali **AND** con uso dell'operatore **nextGEQ** è rappresentato dalla metodologia **TAAT** (Term-At-A-Time). Questo approccio ottimizza il calcolo dell'intersezione prendendo in carico le posting list partendo obbligatoriamente da quella più corta per poi procedere gradualmente fino all'inclusione di quella più lunga, restringendo progressivamente lo spazio di ricerca.

[INSERIRE IMMAGINE: Diagramma a blocchi in cui rettangoli di diversa larghezza, rappresentanti le posting list, vengono intersecati a cascata ordinati dalla più breve alla più lunga]

### Risultati Sperimentali: Query AND

L'efficienza pratica delle query booleane **AND** (in comparazione anche alle strategie OR) si manifesta in maniera eterogenea a seconda delle combinazioni algoritmiche adottate per il salto tra i documenti. I tempi di valutazione effettivi vengono misurati in millisecondi per singola query. La tabella seguente riassume l'esito di sperimentazioni oggettive su dataset documentali di varia entità (TREC 05, TREC 06, ClueWeb09, Gov2), illustrando le discrepanze prestazionali.

| **Metodo**    | **TREC 05** | **TREC 06**  | **ClueWeb09** | **Gov2**     |
| ------------- | ----------- | ------------ | ------------- | ------------ |
| EF single     | 2.1 (+10%)  | 4.7 (+1%)    | 13.6 (-5%)    | 15.8 (-9%)   |
| EF uniform    | 2.1 (+9%)   | 5.1 (+10%)   | 15.5 (+8%)    | 18.9 (+9%)   |
| EF optimal    | 1.9         | 4.6          | 14.3          | 17.4         |
| Interpolative | 7.5 (+291%) | 20.4 (+343%) | 55.7 (+289%)  | 76.5 (+341%) |
| OptPFD        | 2.2 (+14%)  | 5.7 (+24%)   | 16.6 (+16%)   | 21.9 (+26%)  |
| Varint-G8IU   | 1.5 (-20%)  | 4.0 (-13%)   | 11.1 (-23%)   | 14.8 (-15%)  |

### Indici Posizionali per Query a Frase

Il recupero di porzioni di testo non si esaurisce con la semplice ricerca dell'operatore booleano sui termini sparsi; sorge la necessità di risolvere le query a frase dove si esige la precisa sequenza "information retrieval" invece della semplice precondizione logica "information AND retrieval". Per assecondare questa richiesta contestuale, l'infrastruttura dell'indice invertito viene estesa in modo da memorizzare le precise posizioni di ciascuna occorrenza per ogni singolo documento. Analizziamo il comportamento nel documento numero 1, supponendo che la parola "information" ricorra nelle posizioni assolute 10, 40, 55 e 80. All'interno dello stesso elaborato testuale, la parola "retrieval" compare nelle posizioni 30, 45 e 56. Lo scopo della ricerca posizionale è individuare un'occorrenza della prima parola, "information", ad una determinata posizione generica $p$, verificando in contemporanea che una tra le occorrenze di "retrieval" si collochi istantaneamente dopo, nella posizione $p^{+1}$. Passando in rassegna i dati dell'esempio, l'algoritmo segnalerà un riscontro esatto confermando che l'occorrenza di "information" in posizione 55 trova il suo naturale prosieguo sequenziale con il lemma "retrieval" allocato alla posizione 56.

[INSERIRE IMMAGINE: Dettaglio del funzionamento dell'indice posizionale nel documento 1, evidenziando il puntamento e la condizione di adiacenza soddisfatta dai blocchi che segnano le posizioni 55 e 56]

### L'Architettura dell'Elaborazione della Query

Dietro le interfacce interattive dei motori di ricerca opera una complessa architettura a doppio binario, scissa in un flusso "Offline" dedicato alla preparazione strutturale dei dati e un parallelo flusso "Online" predisposto alla soddisfazione immediata delle richieste dell'utente. Nel corso delle operazioni offline in background, la massa primaria di dati, costituita dalla collezione dei documenti ("Document Collection"), viene riversata in un processo di indicizzazione ("Indexing") da cui viene distillato l'indice invertito ("Inverted Index"). Un elaboratore parallelo, il "Feature Processor", agisce a livello documentale popolando una riserva di informazioni detta "Document Features Repository". Simmetricamente, l'infrastruttura estrae dati di addestramento ("Training Data") alimentando il meccanismo di machine learning ("Training") per calibrare uno specifico modello denominato "Learning-to-rank Model".

[INSERIRE IMMAGINE: Architettura di sistema di un motore di ricerca con chiara separazione orizzontale tra l'attività di background offline dei dati e i flussi processuali online verso l'interfaccia utente]

La modalità operativa online innesca la sequenza nel momento in cui l'utente lancia la stringa desiderata ("Query") che, prima di impattare sui dati, viene sottoposta a una fase di estensione linguistica o semantica ("Expanded Query"). Solo allora entra nel blocco vitale denominato "Query Processing", il quale va ad interpellare l'indice invertito generato precedentemente. Il cammino decisionale procede verso la computazione delle caratteristiche e il loro recupero ("Feature Lookup and Computation"), ricollegandosi al repository documentale offline. L'epilogo tecnico dell'architettura si materializza nella funzione di categorizzazione intelligente appresa dal sistema ("Learned Ranking Function"), basata sul modello Learning-to-rank, da cui scaturisce l'effettivo ordine degli URL restituiti sul monitor dell'utente.

Per minimizzare lo spreco prestazionale mantenendo coerenza, il motore processa l'universo documentale scalando la propria capacità di astrazione per gradi. Alla base di questa gerarchia filtrante, dovendo analizzare miliardi di documenti testuali ("1,000,000,000s of documents"), interviene l'elaborazione prettamente Logico-Booleana, dedita a chiarire se, alla luce della stringa di input, i termini interpellati ricorrano effettivamente all'interno dei record o meno, applicando operatori standard come l'AND e l'OR. Dal pool emergente costituito da migliaia di papabili candidati ("1,000s of documents"), agisce il livello intermedio denominato "Simple Ranking" con l'obiettivo di discernere con metodologie primarie, come la formula probabilistica BM25, un set più ristretto di elementi che ospita la stragrande maggioranza dei documenti effettivamente utili. L'eccellenza in termini di perfezionamento dei risultati è demandata alla fase apicale per una porzione drasticamente limata, come gli apripista della classifica ("20 docs"); qui il motore di ricerca si spinge in uno stadio di **Re-Ranking** tramite l'uso del **LEARNING TO RANK**, prodigandosi intensivamente ("Try really hard to get the top of the ranking correct") nel vagliare il comportamento su un altissimo numero di sfaccettature contestuali ("using many signals (features)") e riordinare meticolosamente i vertici della SERP prima di riconsegnarla.

### L'Obiettivo del Top-k Retrieval Esatto

La conclusione delle dinamiche analizzate risiede in gran parte nell'ambito della procedura **Exact Top-k Retrieval**, l'operazione che funge da cerniera tra i semplici indici e il rating di rilevanza finale. L'assunto primario, a fronte dell'interrogazione dell'utente e stabilendo preliminarmente una tolleranza numerica di uscita definita come un parametro discreto $k$ (ad esempio circoscritto a $k=1000$), prevede l'estrazione matematicamente precisa dei **primi $k$ risultati ottimali**. Questi vengono valutati operando sulle diramazioni più indulgenti dell'operatore logico OR, estraendo così i vertici del raggruppamento seguendo funzioni di assegnazione rigorose, di cui il metodo BM25 rappresenta la declinazione più iconica e utilizzata.

**Glossario / Concetti Chiave**

- **DAAT (Document-At-A-Time):** Tecnica di risoluzione delle query che valuta progressivamente documento per documento all'interno delle liste dei termini ricercati.

- **nextGEQ:** Operatore computazionale che accelera le ricerche tra due liste non perfettamente allineate eseguendo salti in avanti per trovare l'elemento successivo "Maggiore o Uguale".

- **TAAT (Term-At-A-Time):** Metodo di risoluzione alternativo che aggredisce l'operazione logica partendo rigorosamente dalle frequenze minori (le posting list più corte).

- **Indici Posizionali:** Mappature sofisticate all'interno di un indice invertito in cui, oltre al rinvio al documento, viene segnalata puntualmente l'ubicazione numerica progressiva della singola parola, rendendo possibile l'individuazione di locuzioni esatte (query a frase).

- **Learning to Rank:** Procedura finale e avanzatissima presente nei flussi online che sfrutta un modello di intelligenza precedentemente istruito sulle feature del testo, dedicata esclusivamente al "Re-Ranking" e perfezionamento della vetta dei risultati presentati all'utente.

---

## Strategie di Recupero per i Top-k Risultati

Questa sezione approfondisce le metodologie utilizzate dai motori di ricerca per selezionare in modo efficiente un sottoinsieme limitato di documenti rilevanti da mostrare all'utente, partendo da una vasta collezione di candidati.

### Recupero Esatto dei Top-k (Exact Top-k Retrieval)

L'obiettivo fondamentale dell'**Exact Top-k Retrieval** è quello di individuare, per una data query e un parametro predefinito $k$ (ad esempio $k=1000$), i primi $k$ risultati che presentano il punteggio più alto. Questo processo viene solitamente eseguito su query di tipo **OR**, utilizzando funzioni di ranking come **BM25** per assegnare un peso numerico a ciascun documento.

La strategia più semplice per gestire questa operazione consiste nell'utilizzare una struttura dati specifica: il **Min-Heap**. Durante la valutazione della query OR, il Min-Heap viene impiegato per mantenere traccia dei $k$ risultati con il punteggio più elevato riscontrati fino a quel momento. La scelta del Min-Heap è motivata dalla necessità di accedere rapidamente al punteggio più basso presente tra i migliori risultati attuali (la radice del heap), facilitando il confronto con i nuovi documenti esaminati.

---

### Gestione dei Valori più Grandi in una Sequenza

Per illustrare il funzionamento del Min-Heap nel contesto del recupero dei $k$ valori più grandi, consideriamo una sequenza di punteggi composta dai valori 2.5, 1.5, 3.0 e 0.5. In questo esempio, l'obiettivo è mantenere i **Top-3** risultati.

[INSERIRE IMMAGINE: Rappresentazione di un Min-Heap con tre nodi contenenti i valori iniziali 2.1 alla radice, e 2.3 e 3.1 come figli]

Immaginiamo che lo stato iniziale del nostro Min-Heap contenga i valori 2.1, 2.3 e 3.1. Il valore alla radice del heap rappresenta la soglia attuale, indicata con **$\tau$** (tau). In questa configurazione, $\tau = 2.1$.

#### Elaborazione del valore 2.5

Quando il sistema incontra il punteggio 2.5 nella sequenza, esegue un confronto con la soglia attuale:

- Poiché $\tau = 2.1$ e $2.1 < 2.5$, il nuovo valore è superiore al minimo presente nel heap.

- Di conseguenza, l'algoritmo esegue l'operazione di **estrazione del minimo** (rimuovendo 2.1) e l'**inserimento** del nuovo valore 2.5 nel heap.

[INSERIRE IMMAGINE: Diagramma che mostra l'aggiornamento del heap: l'uscita del valore 2.1 e l'ingresso del valore 2.5, con il conseguente riordinamento dei nodi]

Dopo questa operazione, il Min-Heap si riorganizza e la nuova soglia $\tau$ diventa il nuovo valore minimo presente tra i primi tre, ovvero $\tau = 2.3$.

#### Elaborazione del valore 1.5

Successivamente, il sistema esamina il valore 1.5:

- In questo caso, il confronto rivela che la soglia attuale $\tau = 2.3$ è maggiore del nuovo valore ($\tau = 2.3 > 1.5$).

- Poiché 1.5 non è sufficientemente alto per entrare nei Top-3, il sistema decide semplicemente di scartarlo (**skip**).

Questo meccanismo permette di processare un'intera sequenza di $n$ documenti mantenendo solo i migliori $k$ in un tempo computazionale pari a **$O(n \log k)$**.

---

**Glossario / Concetti Chiave**

- **Exact Top-k Retrieval**: Procedura per trovare esattamente i primi $k$ documenti con il punteggio più alto secondo una funzione di ranking.

- **Min-Heap**: Struttura dati ad albero che mantiene l'elemento più piccolo alla radice, utilizzata per gestire efficientemente la soglia di sbarramento dei risultati.

- **Soglia $\tau$ (tau)**: Il punteggio minimo necessario affinché un nuovo documento possa essere considerato tra i primi $k$ risultati correnti.

- **Complessità $O(n \log k)$**: Efficienza temporale dell'algoritmo basato su Min-Heap, dove $n$ è il numero totale di documenti e $k$ è il numero di risultati richiesti.

---

## Esecuzione Pratica del Top-k Retrieval: Valutazione Query OR

Questo capitolo illustra operativamente come un sistema di Information Retrieval gestisce una query testuale disgiuntiva, ovvero di tipo OR, combinando l'uso della struttura dati Min-Heap con la scansione parallela delle liste invertite. Osserveremo passo dopo passo come i punteggi parziali dei documenti si sommano e come la soglia di sbarramento si aggiorna dinamicamente.

### Complessità Computazionale del Min-Heap

A completamento di quanto visto nella gestione della coda di priorità per il mantenimento dei migliori documenti, è fondamentale definire il costo operativo di questa strategia. L'efficienza temporale dell'algoritmo basato su Min-Heap, in cui si scartano sistematicamente i valori inferiori alla soglia $\tau$ corrente, garantisce una complessità computazionale pari a $O(n \log k)$ tempo. In questa notazione, $n$ rappresenta il numero totale di documenti esaminati durante lo scorrimento delle liste, mentre $k$ indica la capienza massima del Min-Heap, ovvero la quantità di risultati desiderati dall'utente.

### Analisi di una Query OR Multi-Termine

Per comprendere le meccaniche di valutazione, analizziamo un caso pratico in cui il sistema deve elaborare una query OR composta da quattro termini specifici: "rust", "best", "programming" e "language". Il motore di ricerca recupera dall'indice invertito le quattro liste di posting associate. In questo scenario, ogni elemento della lista non contiene solo l'identificativo del documento (docId), ma è accoppiato al suo punteggio parziale (score) precalcolato per quel termine. Di seguito è riportata la struttura dei dati estratti:

| **Termine**     | **Elementi della Posting List (docId, score)** |
| --------------- | ---------------------------------------------- |
| **rust**        | 15, 2.5 \| 16, 1.5 \| 25, 2.0 \| 45, 1.5       |
| **best**        | 11, 0.3 \| 12, 0.1 \| 13, 0.1 \| 15, 0.2       |
| **programming** | 13, 0.5 \| 15, 1.0 \| 19, 1.0 \| 21, 1.0       |
| **language**    | 10, 0.5 \| 13, 0.9 \| 25, 0.8 \| 29, 1.1       |

[INSERIRE IMMAGINE: Rappresentazione dell'allineamento orizzontale delle quattro posting list per i termini della query, con frecce rosse che indicano l'allineamento dei puntatori sui primi documenti disponibili]

### Inizializzazione e Avanzamento dei Puntatori (DAAT)

Il sistema è configurato per eseguire un Exact Top-k Retrieval con l'obiettivo di trovare il singolo documento migliore in assoluto, impostando quindi il parametro di ricerca su un **Top-1**. Supponiamo che, in una fase precedente dell'elaborazione, il sistema abbia già individuato e salvato nel Min-Heap il documento identificato dal numero 8 ($d = 8$), il quale possiede uno score complessivo pari a 2.1. Questo valore stabilisce la soglia di sbarramento attuale: $\tau = 2.1$.

Adottando una strategia Document-At-A-Time (DAAT), il motore inizia a scorrere parallelamente le liste analizzando i documenti in ordine numerico crescente. Il sistema valuta inizialmente il documento 10 dalla lista "language" (score 0.5) , poi i documenti 11 e 12 dalla lista "best" (score rispettivamente di 0.3 e 0.1). Nessuno di questi, preso singolarmente, ha un punteggio in grado di impensierire l'attuale soglia di 2.1. Procedendo, il sistema aggrega i punteggi per il documento 13, che compare simultaneamente nelle liste "best" (0.1), "programming" (0.5) e "language" (0.9). La somma per il documento 13 risulta essere 1.5, che è ancora inferiore a $\tau = 2.1$, motivo per cui anche questo documento viene ignorato e il Min-Heap rimane invariato.

[INSERIRE IMMAGINE: Scorrimento progressivo in diagonale dei puntatori sulle liste invertite, che evidenzia lo spostamento dalle prime posizioni fino al blocco sul documento 15]

### L'Aggiornamento della Soglia Top-1

L'elaborazione continua fino a quando i puntatori si allineano sul documento identificato dal numero 15. Il sistema rileva la presenza di questo documento su tre differenti liste: "rust" fornisce un contributo significativo con uno score di 2.5 , "best" aggiunge un parziale di 0.2 e "programming" contribuisce con 1.0. Accumulando questi valori ($2.5 + 0.2 + 1.0$), si ottiene uno score globale per il documento 15 pari a 3.7.

A questo punto, l'algoritmo confronta il nuovo score totale con la soglia di sbarramento. Poiché 3.7 è nettamente superiore a 2.1, il documento 8 viene sfrattato dal Min-Heap. Il nuovo detentore della posizione Top-1 diventa il documento 15 ($d = 15$). Di conseguenza, la soglia di sbarramento globale per i futuri documenti analizzati viene innalzata rigorosamente al nuovo limite di $\tau = 3.7$. Questa meccanica, sebbene garantisca l'esattezza matematica del risultato, evidenzia come il costo computazionale rimanga strettamente proporzionale alla lunghezza totale delle liste esaminate, introducendo la necessità per sistemi futuri di metodi di scarto (pruning) più aggressivi.

---

**Glossario / Concetti Chiave**

- **Complessità $O(n \log k)$:** Espressione matematica che descrive il tempo necessario per valutare $n$ elementi mantenendo aggiornata una classifica dei migliori $k$ risultati.

- **Query OR:** Tipo di interrogazione testuale il cui punteggio totale di un documento è calcolato aggregando i punteggi parziali di ogni singolo termine presente al suo interno.

- **Score Parziale:** Il valore numerico (peso) precalcolato e associato a un documento all'interno della singola lista invertita di un termine specifico.

- **DAAT con Accumulatore:** Il processo con cui il motore si sofferma su un singolo identificativo (es. il documento 15) per sommare verticalmente tutti i suoi score parziali prima di passare all'identificativo successivo.

---

## L'Inefficienza del Modello Base e l'Introduzione della Strategia WAND

Questo capitolo analizza i limiti di calcolo dell'approccio standard per il recupero dei documenti e introduce tecniche avanzate di potatura dinamica (pruning), indispensabili per ottimizzare i tempi di risposta dei motori di ricerca moderni senza sacrificare la precisione dei risultati.

### I Limiti dell'Approccio Lineare

Come osservato precedentemente nella valutazione di una query OR, l'obiettivo formale dell'Exact Top-k Retrieval è quello di trovare gli esatti Top-k risultati (ad esempio impostando il parametro k = 1000) basandosi su punteggi generati da funzioni di ranking note, come il BM25. La strategia base si affida all'uso di un Min-Heap per conservare i punteggi più alti man mano che si scorrono i dati. Nonostante il sistema riesca ad aggiornare con successo la classifica (come dimostrato dall'avanzamento dei puntatori fino a eleggere un nuovo documento Top-1 con uno score di 3.7), questo metodo si rivela profondamente inefficiente. Il motivo risiede nel fatto che il costo computazionale cresce in maniera direttamente proporzionale al numero totale di posting presenti in tutte le liste associate ai termini della query. In sostanza, il motore è costretto a ispezionare un numero eccessivo e insostenibile di identificativi documentali.

[INSERIRE IMMAGINE: Scorrimento in profondità dei puntatori sulle quattro liste invertite fino all'allineamento sul documento 15, evidenziando il salto della soglia tau a 3.7]

### L'Algoritmo WAND (Weak AND)

Per superare questo ostacolo prestazionale, nel 2003 i ricercatori Andrei Z. Broder, David Carmel, Michael Herscovici, Aya Soffer e Jason Y. Zien hanno presentato alla conferenza CIKM lo studio intitolato "Efficient query evaluation using a two-level retrieval process". In questo contesto accademico prende forma l'algoritmo **WAND** (Weak AND). Si tratta di una strategia di potatura dinamica (dynamic pruning) strutturata specificamente per autorizzare il sistema a saltare (skip) la valutazione di numerosi identificativi documentali. La grande forza innovativa di WAND risiede nella sua promessa: l'algoritmo garantisce matematicamente di restituire gli esatti risultati Top-k, pur ignorando volontariamente e massicciamente una vasta porzione dei posting. L'idea operativa fondante è logica e rigorosa: data la soglia di sbarramento corrente, indicata dalla variabile $\tau$, il sistema salta l'elaborazione di tutti quei documenti per i quali vi è la certezza matematica che otterranno un punteggio strettamente inferiore a $\tau$.

### Upper Bound e Stima dei Punteggi

Per poter prevedere a priori se un documento supererà o meno la soglia senza doverne sommare tutte le componenti, l'algoritmo WAND richiede un potenziamento della struttura dati: per ogni singola lista di posting viene memorizzato un valore chiamato **Upper Bound** (UB), che corrisponde semplicemente al punteggio più grande registrato in assoluto all'interno di quella specifica lista. L'introduzione degli Upper Bound (UBs) consente di creare delle approssimazioni del punteggio reale di un documento. Se consideriamo una query composta da quattro termini, rappresentata matematicamente come $Q = t_1 t_2 t_3 t_4$ , sappiamo che lo score totale effettivo è calcolato sommando i contributi singoli tramite l'equazione $s(Q, d) = s(t_1, d) + s(t_2, d) + s(t_3, d) + s(t_4, d)$. Usando gli UB, possiamo stimare un limite massimo teorico, sapendo che il punteggio reale sarà sempre minore o uguale alla somma di alcuni UB noti e dei punteggi esatti ricalcolati per le rimanenti variabili, ottenendo la disuguaglianza $s(Q, d) \le UB(t_1) + UB(t_2) + s(t_3, d) + s(t_4, d)$.

### Inizializzazione degli UB nell'Esempio Pratico

Per visualizzare l'applicazione pratica della logica WAND, riprendiamo l'esempio del recupero Exact Top-k per la query OR formata dai termini "rust", "best", "programming" e "language". Modifichiamo l'interfaccia di analisi aggiungendo una colonna apposita per ospitare gli UB. Il nostro scenario di riferimento punta a estrarre un solo documento vincente (Top-1), e supponiamo di avere attualmente in memoria il documento 8 con una soglia di sbarramento fissata a $\tau = 2.1$.
Il primo passo del sistema è calcolare il tetto massimo per la prima parola della query. Ispezionando la lista associata a "rust", il motore legge in sequenza i punteggi 2.5, 1.5, 2.0 e 1.5. Individuato il picco massimo tra questi elementi, il sistema stabilisce in modo definitivo che l'Upper Bound per il termine "rust" è pari a 2.5. Questa informazione garantisce che nessun documento potrà mai ricevere un contributo superiore a 2.5 proveniente da questa specifica lista.

[INSERIRE IMMAGINE: Visualizzazione della lista di posting per il termine "rust", con un indicatore visivo che estrapola il punteggio massimo 2.5 e lo inserisce nella colonna separata dedicata all'Upper Bound]

**Glossario / Concetti Chiave**

- **WAND (Weak AND):** Strategia di pruning dinamico in grado di escludere dall'analisi intere sezioni di posting list, garantendo al contempo l'individuazione esatta dei risultati Top-k.

- **Upper Bound (UB):** Il valore corrispondente al punteggio massimo registrato da un singolo termine all'interno della propria posting list, sfruttato per stimare il potenziale di punteggio di un documento ignoto.

- **Dynamic Pruning:** Meccanica di ottimizzazione informatica che scarta (pota) dinamicamente rami di calcolo improduttivi basandosi sul confronto preventivo con una soglia limite.

---

# L'Ordinamento e la Valutazione Dinamica nell'Algoritmo WAND

Questo capitolo prosegue l'esame dell'algoritmo WAND (Weak AND) per il recupero esatto dei Top-k documenti. Dopo aver compreso il concetto teorico di limite massimo, analizzeremo nel dettaglio la meccanica operativa con cui il sistema organizza le liste di posting e accumula i punteggi per scartare rapidamente i documenti irrilevanti.

### Mappatura degli Upper Bound (UB)

Per applicare concretamente l'algoritmo WAND, il motore di ricerca deve prima mappare il potenziale massimo di ogni termine della query. Riprendendo il nostro scenario di recupero esatto per un solo documento vincente (**Top-1**), sappiamo che l'attuale detentore della prima posizione è il documento identificato con d=8, il quale fissa la soglia di sbarramento a **τ=2.1**.

Il sistema estrapola il punteggio più alto da ciascuna posting list, definendo così gli **Upper Bound (UB)** per i quattro termini in gioco. Come riassunto nella tabella sottostante, il termine "rust" ha un tetto massimo di 2.5, "best" si ferma a 0.3, "programming" a 0.6 e "language" a 1.1.

| Termine         | Elementi della Posting List (docId, score) | UB  |
| --------------- | ------------------------------------------ | --- |
| **rust**        | 15, 2.5 \| 16, 1.5 \| 25, 2.0 \| 45, 1.5   | 2.5 |
| **best**        | 11, 0.3 \| 12, 0.1 \| 13, 0.1 \| 15, 0.2   | 0.3 |
| **programming** | 13, 0.5 \| 15, 0.6 \| 19, 0.6 \| 21, 0.6   | 0.6 |
| **language**    | 10, 0.5 \| 13, 0.9 \| 25, 0.8 \| 29, 1.1   | 1.1 |

### Ordinamento per Identificativo di Documento Corrente (Sort by current docId)

Il vero motore logico di WAND entra in azione riorganizzando la sequenza di valutazione. Invece di analizzare le liste nell'ordine casuale o lessicografico in cui sono state caricate, l'algoritmo esegue un riordino dinamico basato sull'identificativo del documento attualmente puntato in ciascuna lista (**Sort by current docId**).

Osservando i primi elementi disponibili, il puntatore della parola "language" si trova sul documento 10, quello di "best" sul documento 11, per "programming" sul documento 13 e infine per "rust" sul documento 15. Ordinando queste liste in senso crescente in base a questo parametro posizionale, la struttura dati si riallinea presentando prima "language", seguita da "best", "programming" e per ultima "rust".

[INSERIRE IMMAGINE: Rappresentazione visiva delle quattro posting list riordinate dall'alto verso il basso in base al primo docId disponibile: 10, 11, 13 e 15]

### Valutazione delle Soglie e Potatura (Pruning) dei Documenti

A questo punto, il sistema sfrutta l'ordine appena creato per accumulare progressivamente i valori di Upper Bound, verificando se il limite teorico superi la soglia di sbarramento τ=2.1. Questa stima permette di capire se valga o meno la pena calcolare il punteggio reale del documento sotto esame.

Si parte dal primo elemento in lista, il documento 10 associato al termine "language". Il sistema si pone la seguente domanda matematica: è possibile che lo score reale del documento 10 per l'intera query (s(Q,10)) sia maggiore della soglia? La formula applicata è τ=2.1<?s(Q,10)≤UB(language)=1.1. Poiché il valore massimo possibile garantito da quell'unica lista (1.1) è nettamente inferiore a 2.1, l'algoritmo salta immediatamente il documento 10 senza eseguire calcoli complessi.

Procedendo verso il basso, il puntatore successivo si ferma sul documento 11 della lista "best". Il sistema somma l'UB del termine attuale con quello del termine precedente per stabilire il nuovo limite teorico. La disuguaglianza testata diventa τ=2.1<?s(Q,11)≤UB(language)+UB(best)=1.1+0.3=1.4. Anche in questo caso, la somma massima teorica di 1.4 non è in grado di scalfire il valore di 2.1 stabilito dal documento 8 in memoria. Il documento 11 viene scartato a priori.

+1

L'iterazione avanza intercettando il documento 13 sulla lista "programming". L'accumulazione si estende aggiungendo l'UB del nuovo termine, portando l'operazione a τ=2.1<?s(Q,13)≤UB(language)+UB(best)+UB(programming)=1.1+0.3+0.6=2.0. Sorprendentemente, anche accumulando i tetti massimi delle prime tre parole della query, si raggiunge un limite invalicabile di 2.0, che si mantiene strettamente al di sotto della soglia necessaria di 2.1. Di conseguenza, anche il documento 13 viene sottoposto a potatura logica e ignorato dal motore di ricerca, garantendo un enorme risparmio di risorse computazionali pur mantenendo l'assoluta esattezza del risultato atteso.

+1

---

**Glossario / Concetti Chiave**

- **Sort by current docId:** Il processo di riordino dinamico delle liste invertite basato esclusivamente sul valore numerico dell'identificativo del documento attualmente sotto il cursore di lettura.

- **Accumulazione degli Upper Bound:** Somma progressiva dei tetti massimi (UB) dei termini ordinati per verificare teoricamente il potenziale punteggio del documento analizzato.

- **Potatura Dinamica (Dynamic Pruning):** L'azione pratica compiuta dall'algoritmo WAND quando ignora interamente il calcolo esatto di un documento, avendo dimostrato matematicamente che la somma dei suoi Upper Bound è inferiore alla soglia di sbarramento.

---

# Il Limite di WAND e l'Introduzione di MaxScore

Questo capitolo conclude l'esempio applicativo dell'algoritmo WAND, illustrando il momento esatto in cui il sistema è costretto a eseguire una valutazione completa del punteggio, per poi introdurre **MaxScore**, una strategia di potatura alternativa che si fonda sulla divisione logica delle liste di posting.

### La Valutazione Obbligatoria in WAND

Riprendendo il calcolo accumulato nella fase precedente, il motore di ricerca aveva sommato gli Upper Bound (UB) dei primi tre termini ordinati ("language", "best", "programming"), raggiungendo un limite teorico di 2.0. Questo valore non era sufficiente a superare la soglia τ di 2.1 stabilita dal documento temporaneamente in prima posizione (documento 8). Il processo iterativo di WAND, tuttavia, prevede l'aggiunta dell'ultimo termine rimanente, ovvero "rust". Questo specifico termine possiede un Upper Bound decisamente elevato, quantificato in 2.5.

+4

[INSERIRE IMMAGINE: Visualizzazione dell'accumulazione finale degli Upper Bound con l'inserimento del termine "rust" e il superamento della soglia limite di 2.1]

Aggiungendo quest'ultimo dato, la formula di verifica cambia drasticamente esito. L'equazione calcolata dal sistema diventa: τ=2.1<?s(Q,13)≤UB(language)+UB(best)+UB(programming)+UB(rust)=1.1+0.3+0.6+2.5=4.5. Poiché il risultato totale di 4.5 supera ampiamente la soglia di sbarramento di 2.1, il sistema perde la certezza matematica che il documento possa essere scartato a priori. Di conseguenza, la potatura dinamica si interrompe e il motore stabilisce la necessità assoluta di valutare il documento completo, segnalando il comando di analizzare il documento 15 ("Need to evaluate document 15!").

+4

### MaxScore: Una Strategia Alternativa di Pruning Dinamico

Oltre a WAND, il mondo dell'Information Retrieval si avvale di altre metodologie per velocizzare l'esecuzione delle query. Tra queste spicca **MaxScore**, definita come un'ulteriore strategia di potatura dinamica. Esattamente come il suo predecessore, questo algoritmo garantisce il calcolo dei risultati Top-k esatti, permettendo al contempo di ignorare (saltare) un vasto numero di posting improduttivi. Le basi teoriche di MaxScore sono state delineate e pubblicate nel 1995 da Howard Turtle e James Flood, all'interno del saggio "Query evaluation: Strategies and optimizations" sulla rivista Information Processing & Management.

+2

L'intuizione alla base di MaxScore si discosta dal concetto di accumulo visto in WAND. Data la consueta soglia corrente τ, questo algoritmo opera una spaccatura, suddividendo le liste di posting in due insiemi separati: le liste **essenziali** (essential) e quelle **non essenziali** (non-essential). Le liste considerate non essenziali sono quelle associate agli Upper Bound più piccoli. Questa separazione avviene assicurandosi che la somma dei massimali delle liste non essenziali si mantenga ad un livello tale da non impensierire la soglia di sbarramento, permettendo al motore di evitare calcoli su documenti che non figurano nelle liste principali.

+2

### Ordinamento e Dati in MaxScore

Per comprendere l'applicazione pratica di MaxScore, analizziamo i dati in ingresso. Il sistema mantiene come obiettivo l'estrazione del Top-1 assoluto. Il documento 8 in memoria continua a imporre una soglia τ pari a 2.1. Nella tabella seguente, elaborata per questo specifico test, si nota che i punteggi della lista "programming" presentano un Upper Bound ricalcolato pari a 1.0 (mentre nell'esempio WAND precedente si attestava a 0.6).

+3

| Termine         | Elementi della Posting List (docId, score) | UB  |
| --------------- | ------------------------------------------ | --- |
| **rust**        | 15, 2.5 \| 16, 1.5 \| 25, 2.0 \| 45, 1.5   | 2.5 |
| **best**        | 11, 0.3 \| 12, 0.1 \| 13, 0.1 \| 15, 0.2   | 0.3 |
| **programming** | 13, 0.5 \| 15, 1.0 \| 19, 1.0 \| 21, 1.0   | 1.0 |
| **language**    | 10, 0.5 \| 13, 0.9 \| 25, 0.8 \| 29, 1.1   | 1.1 |

L'approccio operativo iniziale di MaxScore differisce sensibilmente da WAND. Se in precedenza le liste venivano ordinate osservando l'identificativo del documento, l'algoritmo MaxScore impone invece che il blocco di dati venga riordinato rigorosamente in base al valore dell'Upper Bound ("Sort by UB"). Questa mossa strutturale è un prerequisito fondamentale per riuscire a isolare correttamente i termini essenziali da quelli non essenziali.

+2

**Glossario / Concetti Chiave**

- **Valutazione Obbligatoria (Evaluate):** In WAND, l'azione imposta al sistema quando l'accumulo dei massimali teorici di punteggio supera la soglia τ, costringendo il calcolo reale dello score del documento.
  
  +1

- **MaxScore:** Tecnica di potatura dinamica formalizzata da Turtle e Flood (1995) che aggira l'elaborazione dei documenti partizionando logicamente le posting list.
  
  +1

- **Liste Essenziali e Non Essenziali:** Le due macro-categorie in cui MaxScore divide i termini di ricerca. Le liste con gli UB più bassi compongono l'insieme non essenziale.

- **Sort by UB:** Il riordino preliminare delle liste di posting operato da MaxScore in funzione esclusiva dell'entità dei rispettivi tetti massimi.

---

# Il recupero esatto Top-k: L'algoritmo MaxScore

Questa sezione introduce il funzionamento base dell'algoritmo MaxScore per l'operazione di "Exact Top-k Retrieval", ovvero il recupero esatto dei migliori $k$ documenti pertinenti a una ricerca. L'algoritmo ottimizza il calcolo dei punteggi dividendo i termini della query e le loro occorrenze in due categorie distinte.

### Slide 1: Stato iniziale e parametri di base

Il processo di recupero si basa sull'uso di liste invertite, le quali contengono le coppie di identificatori del documento e il relativo punteggio di pertinenza. Per ottimizzare il processo, per ogni termine della query viene precalcolato un limite superiore, definito **UB** (Upper Bound), che rappresenta il punteggio massimo assoluto che quel termine può generare.

I dati di partenza per la query di esempio, composta dai termini "rust", "language", "programming" e "best", sono i seguenti:

| **Termine**     | **Liste Invertite (docId, punteggio)**     | **UB** |
| --------------- | ------------------------------------------ | ------ |
| **rust**        | (15, 2.5), (16, 1.5), (25, 2.0), (45, 1.5) | 2.5    |
| **language**    | (10, 0.5), (13, 0.9), (25, 0.8), (29, 1.1) | 1.1    |
| **programming** | (13, 0.5), (15, 1.0), (19, 1.0), (21, 1.0) | 1.0    |
| **best**        | (11, 0.3), (12, 0.1), (13, 0.1), (15, 0.2) | 0.3    |

L'obiettivo di questa esecuzione è trovare il singolo documento più pertinente, operando quindi in modalità **Top-1**. Le condizioni iniziali impostano il documento corrente in esame a $d = 8$ e stabiliscono una soglia minima di sbarramento $\tau = 2.1$.

### Slide 2-4: Liste essenziali e non essenziali

[INSERIRE IMMAGINE: Diagramma che mostra le quattro liste invertite allineate, separate da una linea tratteggiata, con una freccia rossa direzionale che attraversa verticalmente i primi documenti di ciascuna lista.]

L'algoritmo MaxScore accelera la ricerca suddividendo strategicamente le liste in due gruppi. Nell'esempio, i termini "rust" e "language" sono classificati come **liste essenziali** , mentre "programming" e "best" costituiscono le **liste non essenziali**. La direttiva operativa fondamentale è quella di processare le liste essenziali seguendo l'approccio *Document-at-a-Time* (Daat) con operatore logico OR. Ad ogni passo di questa scansione, il sistema effettua un controllo per verificare se sia possibile ignorare il documento corrente senza doverne calcolare il punteggio esatto.

### Slide 5-7: Valutazione del documento 10

Durante la progressione, il sistema analizza il documento identificato con il numero 10, che appare nella lista del termine "language". Il calcolo verifica se il punteggio teorico massimo del documento 10 per l'intera query, indicato come $s(Q,10)$, possa superare l'attuale soglia $\tau = 2.1$. Di conseguenza, si somma l'Upper Bound delle liste non essenziali al punteggio effettivo che il documento possiede nella lista essenziale in cui è stato trovato. La formula di controllo è: $\tau=2.1<?s(Q,10)\le UB(programming)+UB(best)+s(1anguage,10)$ Sostituendo i valori ricavati dalla tabella iniziale si ottiene l'equazione $1.0 + 0.3 + 0.5 = 1.8$. Poiché il limite superiore totale di 1.8 è inferiore alla soglia richiesta di 2.1, il documento 10 non ha alcuna possibilità matematica di entrare nella classifica Top-1 e viene immediatamente scartato.

### Slide 8-10: Valutazione del documento 13

Il controllo successivo si sposta sul documento 13, anch'esso reperito nella lista essenziale del termine "language". Si riapplica la stessa logica di sbarramento per vedere se il punteggio potenziale massimo del documento $s(Q,13)$ superi la soglia $\tau = 2.1$. La formula si aggiorna con il punteggio specifico del nuovo documento: $\tau=2.1<?s(Q,13)\le UB(programming)+UB(best)+s(language,13)$ Il calcolo aggiornato produce $1.0 + 0.3 + 0.9 = 2.2$. In questo caso, la soglia di 2.1 è strettamente minore del punteggio massimo potenziale di 2.2. Questo significa che il documento 13 potrebbe potenzialmente essere il miglior candidato e, di conseguenza, il sistema non può scartarlo ma dovrà valutare anche le altre occorrenze per calcolarne il punteggio finale.

### Concetti Chiave

- **Algoritmo MaxScore**: Tecnica per ottimizzare l'Exact Top-k Retrieval, riducendo i calcoli necessari attraverso il partizionamento dei termini.

- **UB (Upper Bound)**: Il punteggio massimo che ogni singolo termine può fornire a un documento, precalcolato prima dell'esecuzione.

- **Partizionamento delle Liste**: Divisione tra liste **essenziali** e **non essenziali** per stimare rapidamente il punteggio massimo potenziale di un documento in esame.

- **Soglia $\tau$**: Valore dinamico di punteggio che determina se un documento ha il potenziale matematico per entrare nella classifica dei Top-k risultati.

---

# L'algoritmo MaxScore: Calcolo dei punteggi e Risultati Sperimentali

Questa sezione conclude l'analisi dell'esecuzione dell'algoritmo MaxScore, mostrando come viene completata la valutazione dei documenti candidati e presentando un confronto prestazionale con altri metodi di recupero.

### Il calcolo effettivo per il documento 13

Come visto nel passaggio precedente, il documento $d = 13$ aveva superato il controllo preliminare, poiché il suo limite superiore teorico di 2.2 era maggiore della soglia $\tau = 2.1$. Questo rende necessario calcolare il punteggio esatto accumulato dal documento in tutte le liste. Dalla disamina delle liste invertite, il documento 13 riceve 0.9 punti da "language" , 0.5 punti da "programming" e 0.1 punti da "best". Sommando questi contributi reali, si ottiene un punteggio totale effettivo pari a 1.5. Dato che questo valore (1.5) non supera l'attuale soglia di 2.1, l'algoritmo non aggiorna la soglia e scarta l'ipotesi di rendere il documento 13 il nuovo candidato Top-1.

### Valutazione del documento 15

Il processo procede nell'esplorazione e il prossimo identificatore incontrato nelle liste essenziali è il documento 15, situato nella lista del termine "rust". Si riapplica immediatamente il test di sbarramento per verificare se vale la pena calcolarne il punteggio complessivo. La formula confronta la solita soglia $\tau$ (2.1) con il limite superiore specifico per il documento 15. In questo caso, il calcolo somma l'Upper Bound delle liste non essenziali (1.0 per "programming" e 0.3 per "best") al punteggio esatto che il documento possiede nella lista in cui è stato trovato, ovvero 2.5 per "rust". L'equazione diviene quindi $1.0 + 0.3 + 2.5 = 3.8$. Poiché il punteggio potenziale di 3.8 è ampiamente superiore alla soglia di 2.1, il documento 15 si rivela un candidato eccellente e l'algoritmo dovrà procedere con il calcolo del suo punteggio esatto.

### Prestazioni e Confronto Sperimentale

[INSERIRE IMMAGINE: Due grafici a barre affiancati che illustrano la disposizione e la dimensione di vari blocchi, con linee tratteggiate e valori numerici (es. 4, 8, 7, 2, 1) sopra le colonne].

La parte finale della lezione sposta l'attenzione sull'efficienza di questi algoritmi, analizzando i risultati sperimentali per un recupero di tipo Top-10 sul dataset Gov2 Trec05. L'analisi si concentra sul tempo medio in millisecondi per query e sullo spazio aggiuntivo richiesto. Nelle esecuzioni sono state testate configurazioni con 5 blocchi, sia di dimensione fissa (pari a 3) che di dimensione variabile.

Il seguente prospetto riassume le prestazioni all'aumentare dei termini di ricerca:

| **Algoritmo** | **2 termini** | **3 termini** | **4 termini** | **5 termini** | **6+ termini** | **Avg (Gov2 Trec05)** | **Space** |
| ------------- | ------------- | ------------- | ------------- | ------------- | -------------- | --------------------- | --------- |
| **RankedOR**  | 23.6          | 76.5          | 147.9         | 235.4         | 418.7          | 106.7                 | 0.00      |
| **WAND**      | 5.1           | 5.9           | 7.0           | 8.8           | 17.8           | 7.0                   | 0.22      |
| **MaxScore**  | 4.7           | 6.0           | 7.1           | 9.2           | 14.2           | 6.6                   | 0.22      |

I dati dimostrano in modo inequivocabile che MaxScore e WAND dominano rispetto a RankedOR. Mentre RankedOR vede i suoi tempi esplodere linearmente con l'aggiunta di termini alla query (raggiungendo i 418.7 millisecondi per 6 o più termini), MaxScore si mantiene estremamente performante, con un tempo medio generale di soli 6.6 millisecondi e un costo spaziale minimo di 0.22. Questo evidenzia la straordinaria capacità delle euristiche con soglia di tagliare rami di calcolo superflui.

### Concetti Chiave

- **Calcolo del punteggio effettivo**: Il passaggio in cui, superato il controllo dell'Upper Bound, si sommano i valori reali del documento in tutte le liste invertite.

- **Valutazione Sperimentale (Top-10)**: Dimostrazione pratica dell'efficacia degli algoritmi di recupero misurata in millisecondi per query su dataset reali.

- **Configurazione a Blocchi**: Strutturazione dei dati, a dimensione fissa o variabile, usata durante i test di ottimizzazione per migliorare i tempi di recupero.

- **Scalabilità**: La capacità di algoritmi come MaxScore e WAND di mantenere bassi i tempi di latenza anche al crescere del numero dei termini presenti nella query utente.

---