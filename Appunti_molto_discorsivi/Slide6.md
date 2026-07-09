### Architettura Generale e Flusso di Dati

Il processo di Learning to Rank si inserisce all'interno di un'architettura di ricerca complessa, strutturata su due macro-fasi parallele: una fase **Offline** e una fase **Online**.

Nella fase offline, si parte da una **Document Collection** che viene sottoposta a una procedura di **Indexing** per generare l'**Inverted Index**, fondamentale per il recupero rapido delle informazioni. Parallelamente, la stessa collezione di documenti viene analizzata da un **Feature Processor**, il cui scopo è estrarre caratteristiche utili e salvarle in un **Document Features Repository**. Sempre offline, un set di **Training Data** viene utilizzato in un processo di **Training** per generare e istruire il vero e proprio **Learning-to-rank Model**.

Nella fase online, ovvero quando il sistema è in esecuzione per l'utente, il flusso inizia con l'inserimento di una **Query**. Questa viene inizialmente trasformata in una **Expanded Query** per migliorarne l'efficacia. Il passo successivo è il **Query Processing**, che interroga direttamente l'**Inverted Index** precedentemente costruito. Una volta individuati i documenti candidati, il sistema esegue una **Feature Lookup and Computation**, attingendo le caratteristiche necessarie dal **Document Features Repository**. Infine, queste feature vengono passate alla **Learned Ranking Function** (guidata dal modello addestrato) per calcolare il punteggio finale e produrre la pagina dei risultati mostrata a schermo.

![[Pasted image 20260417114521.png]]

### Decomposizione delle Funzioni di Ranking e Modelli Bag-of-Words

Per comprendere a fondo come operano le **General ranking functions**, possiamo utilizzare una decomposizione basata sulle rappresentazioni.
In questo modello teorico, abbiamo una **Query q** che viene elaborata in una **Query representation** $\phi$, e un **Document d** che genera una **Document representation** $\psi$. Inoltre, l'interazione diretta tra i due produce una **Query-document representation $\eta$**. Queste tre rappresentazioni alimentano insieme una **Aggregation function f**, la quale calcola e restituisce il **Relevance score $s(q,d)$**.

Questo schema si semplifica drasticamente quando si analizzano le **BOW ranking functions**, ovvero le classiche funzioni basate sui modelli Bag-of-Words. In queste architetture, query e documenti sono rappresentati unicamente come vettori sparsi (**sparse BOW vectors**), trattati essenzialmente come multi-insiemi di parole. Di conseguenza, **non ci sono query-document features**; la componente relativa alla **Query-document representation $\eta$** viene completamente scartata dal processo di calcolo. Esempi classici di questa specifica funzione di aggregazione $f$ sono il **cosine** similarity e l'algoritmo **BM25**. Queste rappresentazioni sparse sono fisicamente archiviate negli inverted index, che costituiscono la vera e propria spina dorsale (**backbone**) dei motori di ricerca web commerciali (**commercial Web search engine**).

![[Pasted image 20260417114644.png]]

### Estensione del Framework e Integrazione di Nuovi Segnali

Constatato che finora query e documenti sono stati considerati come meri multi-insiemi di parole, è naturale chiedersi se esistano altre istanze applicabili al nostro framework generale per superare questo limite. La risposta è affermativa: esiste infatti una grande quantità di altri potenziali segnali da poter utilizzare.

Quali potrebbero essere queste idee aggiuntive?
Lato documento, si può sfruttare il fatto che esso può avere dei campi strutturati, può essere suddiviso in zone o, ancora, può essere arricchito con dati testuali esterni, come ad esempio il testo dei link in ingresso (**anchors**). Si rivelano preziose anche informazioni aggiuntive di natura strutturale e comportamentale, come i link in entrata (**In-Links**), i link in uscita (**Out-Links**), il valore di **PageRank**, il numero di **clicks** ricevuti o i **social links**. Lato query, invece, si possono integrare segnali quali il numero di termini inseriti (**# terms**), la popolarità della ricerca nei log del motore (**popularity in query logs**) o le informazioni estratte dal profilo dell'utente (**user profile info**). Reintroducendo tutti questi dati nel modello, il framework torna a sfruttare a pieno tutte le sue componenti, reintegrando attivamente la **Query-document representation $\eta$** nel calcolo della funzione di aggregazione.

![[Pasted image 20260417114750.png]]

### L'Apprendimento della Funzione tramite Machine Learning

L'inclusione di tutti questi segnali porta a un interrogativo critico: **possiamo imparare f?**
Imparare questa complessa funzione di aggregazione significa affidarsi alle tecniche di **Machine Learning**.

Secondo la definizione di Wikipedia, il Machine learning è un campo dell'informatica che utilizza tecniche statistiche per consentire ai sistemi informatici di apprendere. Questo si traduce nella capacità di migliorare progressivamente le prestazioni su un compito specifico attingendo dai dati, senza dover programmare il sistema in modo esplicito. Questo approccio metodologico implica necessariamente due precondizioni fondamentali: la disponibilità e l'esistenza di dati 
(**Existence of data**), e la definizione formale di un problema di ottimizzazione (**Optimization problem**), che richiede di stabilire un task specifico e una metrica di misurazione (**task? measure?**).

A seconda che al sistema venga fornito o meno un "segnale" di apprendimento o un "feedback", le attività di Machine Learning si dividono in due ampie categorie:

- **Supervised learning**: In questo scenario, al computer vengono presentati degli input di esempio accompagnati dai rispettivi output desiderati, forniti da una sorta di "insegnante". L'obiettivo dell'algoritmo è imparare una regola generale capace di mappare correttamente i nuovi input verso i giusti output. Per il contesto dell'Information Retrieval e del ranking, ci troviamo di fronte a problemi di classificazione o regressione (**Classification / Regression problems**).

- **Unsupervised learning**: Al contrario dell'apprendimento supervisionato, in questo caso non vengono fornite etichette all'algoritmo, lasciando al sistema il compito di trovare autonomamente una struttura intrinseca all'interno dei dati di input.

### Glossario e Concetti Chiave

- **Learning to Rank**: L'applicazione di modelli addestrati (Machine Learning) per ottimizzare la funzione di aggregazione al fine di ordinare i documenti in base alla loro rilevanza rispetto a una determinata query.

- **Bag-of-Words (BOW)**: Approccio classico in cui query e documenti sono gestiti come semplici insiemi sparsi di parole (es. tramite algoritmi come BM25), escludendo l'uso di feature relazionali o comportamentali complesse.

- **Machine Learning**: Disciplina informatica che sfrutta la statistica per permettere ai calcolatori di apprendere e migliorare in specifiche attività tramite l'osservazione dei dati, senza programmazione esplicita.

- **Supervised Learning**: Modalità di apprendimento automatico in cui il modello viene istruito fornendogli esempi chiari di coppie input-output (es. problemi di classificazione e regressione) per fargli dedurre una regola di mappatura generale.

---

### Terminologia di Base e Fasi dell'Apprendimento

Un tipico task di apprendimento supervisionato mappa gli input verso gli output desiderati. Un esempio classico, seppur distante dai motori di ricerca, è fornire al sistema immagini per fargli imparare a distinguere tra un cane e un gatto.

All'interno di un dataset, ogni singolo elemento da analizzare viene definito **osservazione** o **istanza** (**observation** o **instance**). Queste istanze sono descritte e caratterizzate da un insieme di **feature**, ovvero proprietà specifiche misurabili. 

Nell'apprendimento supervisionato, ogni istanza è accompagnata da un'etichetta, definita **label**, che rappresenta la "soluzione" o il valore da prevedere. Sfruttando lo spazio di queste feature e la supervisione delle label, i problemi di Machine Learning si dividono in due rami principali: i task di **Regression** (regressione), dove l'obiettivo è prevedere una specifica quantità numerica reale, e i task di **Classification** (classificazione), in cui si assegnano etichette discrete a istanze di dati la cui categoria non è conosciuta in anticipo.

Tutte queste attività di apprendimento (sia supervisionate che non) si strutturano in due fasi temporali e logiche ben distinte. La prima è la **training phase** (fase di addestramento), che avviene tipicamente offline. In questo momento il modello viene costruito, o per usare il gergo tecnico, si esegue il "fit a model on a given set of data". La seconda è la **testing phase** (fase di test), generalmente eseguita online direttamente per l'utente , in cui il modello precedentemente addestrato viene impiegato per effettuare le previsioni vere e proprie, ovvero "to predict class/label of a given set of data".

### Il Ciclo di Apprendimento: Un Parallelo con i Puzzle

Per comprendere intuitivamente questo processo di calibrazione degli algoritmi, possiamo fare un parallelismo con i classici giochi di enigmistica per bambini.


Per ogni puzzle, abbiamo a disposizione il problema stesso e, in fondo al libro, la sua soluzione corretta. Di fronte alla sfida, il nostro cervello produce inizialmente un tentativo di soluzione e, in un secondo momento, lo confrontiamo con quella esatta. Osservando le differenze tra la nostra risposta e quella reale, riusciamo a sintonizzare il nostro pensiero e ad apprendere la logica corretta. Ripetendo questa operazione ("ad libitum") per molti giochi, diventiamo sempre più bravi.

Traducendo questa semplice metafora nel framework formale del Machine Learning, un'istanza di addestramento derivante dall'ambiente (**Environment**) viene presentata al sistema di apprendimento (**Learning system**). L'algoritmo produce quindi in uscita una previsione sul momento, ovvero un **Actual output**. Contemporaneamente, una sorta di "Teacher" (Insegnante) fornisce l'output desiderato. La differenza matematica tra la previsione appena calcolata e l'etichetta reale dell'istanza viene elaborata da un sommatore (**Adder**) e prende il nome di **loss** o **error** (errore). L'importanza vitale della loss sta nel fatto che viene "restituita" all'algoritmo e utilizzata per aggiornare i parametri interni del suo modello. Questo meccanismo ciclico viene ripetuto per tutte le istanze che compongono il dataset di addestramento. Quando l'intero dataset viene processato, diciamo di aver completato un ciclo; e poiché il processo intero viene ripetuto svariate volte per ottimizzare l'algoritmo, misuriamo questo scorrere del tempo in **learning iterations** o **training epochs** (epoche di addestramento).

![[Pasted image 20260417115910.png]]

### Il Task del Learning to Rank (LtR)

Cosa succede quando applichiamo queste dinamiche al recupero delle informazioni? L'obiettivo del **Learning to Rank (LtR)** è produrre e ottimizzare le liste ordinate dei risultati. Per fare ciò, la fase di addestramento necessita di un training set di grandi dimensioni, composto da interrogazioni degli utenti affiancate all'ordinamento ideale dei documenti rispetto ad esse.

![[Pasted image 20260417115945.png]]

Invece di processare singole parole, il sistema gestisce l'interazione tra la query "q" e un documento "d" rappresentandola come uno specifico **Feature vector** (ad esempio denotato come $<q,d> = <f0, f1...>$), che funge da input per il modello. Questo modello matematico funziona operativamente come una scatola nera (**Black Box**) che riceve query e vari documenti, restituendo infine una singola **ranked list**. Per guidare questa "scatola nera" durante il training, si usano etichette numeriche "y" che indicano il grado di pertinenza reale; un esempio tipico sfrutta una scala a cinque valori, che va da 0 (documento del tutto irrilevante) fino a 4 (documento perfettamente rilevante). È cruciale evidenziare una dinamica controintuitiva ma fondamentale del Learning to Rank: benché i modelli di intelligenza artificiale addestrati agiscano tecnicamente come regressori che assegnano un punteggio numerico a ogni singolo candidato, lo scopo ultimo e misurabile del sistema resta esclusivamente quello di indovinare l'ordinamento generale della lista, non di replicare precisamente quelle specifiche etichette numeriche.

![[Pasted image 20260417120029.png]]

### Le Feature nei Sistemi di Ranking Moderni

Il successo di un sistema basato su feature vector dipende da quanti e quali segnali si riescono a raccogliere. Già molto tempo fa (in un articolo del New York Times datato 3 giugno 2008), l'ingegnere Amit Singhal rivelò che Google stava utilizzando oltre 200 diverse feature all'interno del suo motore. Questa grande famiglia di parametri comprende indicatori fisici e strutturali, come il numero di link in uscita presenti sulla pagina, il numero totale di immagini, la lunghezza del documento testuale o la lunghezza dell'URL. Si valutano anche dettagli microscopici ma utili per l'anti-spam, come la presenza del carattere tilde ('~') all'interno dell'indirizzo Web , oltre a indicatori di freschezza del contenuto come la data dell'ultima modifica (Page edit recency) e misurazioni globali di autorità come il PageRank. Ad alimentare questi modelli si aggiungono fattori di match testuale (come il punteggio BM25, la comparsa in grassetto o a colori della parola cercata e la sua frequenza logaritmica nei link diretti alla pagina) e importantissimi fattori comportamentali, quali il conteggio dei click generali, i click in corrispondenza della query specifica e il tempo speso dall'utente a leggere il risultato (url dwell time).

Tutte queste innumerevoli feature vengono categorizzate concettualmente dal framework in tre grandi macrogruppi. Il primo è formato dalle **query-only features**: si tratta di caratteristiche che assumono lo stesso valore per ogni documento analizzato in quella sessione, essendo legate esclusivamente alla tipologia o alla lunghezza della query dell'utente. Il secondo raggruppamento è quello delle **query-independent features**, che raggruppa le caratteristiche intrinseche al documento il cui valore rimane costante a prescindere da chi o cosa stia cercando in quel momento. Ne sono classici esempi il PageRank della pagina e la lunghezza del suo URL. Infine, le più dinamiche sono le **query-dependent features**: sono quelle metriche generate in tempo reale dall'interazione tra la richiesta e il documento, come il punteggio BM25, la frequenza della query in grassetto e le statistiche dei click passati per quella specifica correlazione query-URL.

![[Pasted image 20260417120217.png]]

### Glossario e Concetti Chiave

- **Feature Vector**: La rappresentazione matematica (un vettore di caratteristiche e attributi numerici) dell'interazione tra una data query e un documento, passata in ingresso al sistema di Machine Learning.

- **Observation / Instance**: Un singolo esempio o "caso di studio" fornito all'algoritmo all'interno del dataset di addestramento.

- **Loss / Error**: La differenza calcolata tra la previsione generata attualmente dal modello e il risultato corretto atteso, indispensabile per aggiornare iterativamente il sistema.

- **Training Epoch**: Un giro completo di elaborazione e calibrazione degli errori applicato sull'intero dataset di addestramento a disposizione.

- **Query-dependent feature**: Una metrica di ranking che assume valore solo intersecando i dati della pagina analizzata con lo specifico termine ricercato dall'utente (es. BM25).

---

### La Pipeline del Learning to Rank e i Vincoli di Sistema

Dal punto di vista operativo, l'integrazione di un modello di Learning to Rank all'interno di un motore di ricerca avviene comunemente attraverso una pipeline strutturata in due fasi principali, definita **First step** e **Second step**. Inizialmente, la query dell'utente viene processata da un **Base Ranker**, il quale interroga il **Document Index** per estrarre rapidamente un sottoinsieme iniziale di **N docs** (N documenti candidati). Successivamente, questo primo bacino di risultati passa al **Top Ranker**, che sfrutta il **Learning to Rank Algorithm** e i dati provenienti dal repository delle **Features** per riordinare e selezionare un numero più ristretto di **K docs**, i quali andranno a comporre la **Results Page(s)** finale mostrata all'utente.

Questa architettura a due stadi è dettata da stringenti vincoli infrastrutturali. Le considerazioni sul budget, inteso in termini di tempo e risorse di calcolo, sono estremamente importanti per i motori di ricerca commerciali (**commercial WSEs**). Il costo computazionale dei modelli LtR, essendo sensibilmente più elevato rispetto a funzioni semplici, deve essere rigorosamente inserito nel budget di tempo disponibile per elaborare il flusso di query in entrata (**incoming stream**). Questo fattore impatta direttamente sul **throughput** (la capacità di smaltimento) del sistema, motivo per cui la comunità accademica dell'Information Retrieval ha iniziato solo di recente a studiare ottimizzazioni a basso livello per ridurre i tempi di esecuzione di specifiche famiglie di ranker LtR.

![[Pasted image 20260417120317.png]]

### Un Esempio Pratico: Classificazione per l'Ad Hoc IR

Per tradurre questi concetti in pratica, consideriamo l'utilizzo di una tecnica di classificazione per il recupero di informazioni (**ad hoc IR**). Il punto di partenza consiste nel raccogliere un corpus di addestramento formato da triple **(q, d, r)**, dove le prime due variabili rappresentano le coppie query-documento, convertite in vettori di feature $x=<q,d>=<f_0, f_1, ...>$, e l'ultima variabile $r$ rappresenta la rilevanza. Sebbene la rilevanza possa assumere un formato multi-classe (tipicamente con 3-7 valori), in questo esempio semplificato la considereremo binaria, ossia rilevante o non rilevante.

Ipotizziamo di valutare le coppie query-documento basandoci esclusivamente su due feature chiave, definendo il vettore $x=(\alpha,\omega)$. In questa configurazione, il parametro **$\alpha$** rappresenta il **cosine similarity** (il punteggio basato sulla somiglianza del coseno), mentre **$\omega$** indica la minima ampiezza della finestra di query (**min query window sz**). Quest'ultima è definita come la porzione di testo più breve nel documento che include tutte le parole cercate, indipendentemente dal loro ordine, ed è un fattore di ponderazione essenziale per misurare la prossimità dei termini (**term proximity**). Il nostro obiettivo è quindi istruire un modello di Machine Learning per prevedere la classe di rilevanza $r$ per ogni data coppia.

La tabella seguente illustra un esempio di questo set di dati:

| **example** | **docID** | **query**              | **cosine score α** | **term proximity ω** | **judgment** |
| ----------- | --------- | ---------------------- | ------------------ | -------------------- | ------------ |
| $\Phi_1$    | 37        | linux operating system | 0.032              | 3                    | relevant     |
| $\Phi_2$    | 37        | penguin logo           | 0.02               | 4                    | nonrelevant  |
| $\Phi_3$    | 238       | operating system       | 0.043              | 2                    | relevant     |
| $\Phi_4$    | 238       | runtime environment    | 0.004              | 2                    | nonrelevant  |
| $\Phi_5$    | 1741      | kernel layer           | 0.022              | 3                    | relevant     |
| $\Phi_6$    | 2094      | device driver          | 0.03               | 2                    | relevant     |
| $\Phi_7$    | 3191      | device driver          | 0.027              | 5                    | nonrelevant  |

Per discriminare i documenti, possiamo definire una **funzione di punteggio lineare** espressa dall'equazione $Score(d,q) = a\alpha + b\omega$. Trattandosi di un problema simile alla text classification, il classificatore lineare dovrà determinare in autonomia i pesi dei parametri $a$ e $b$, oltre a una soglia limite **$\Theta$**. Il sistema deciderà quindi di etichettare la coppia come rilevante se il risultato dell'equazione supera tale soglia ($Score(d,q) > \Theta$) o come irrilevante in caso contrario 
($Score(d,q) \le \Theta$).

![[Pasted image 20260417120600.png]]

### Le Tre Famiglie di Approcci al Learning to Rank

Nel panorama del Learning to Rank, gli algoritmi si dividono in tre grandi approcci metodologici, distinti in base al modo in cui viene calcolata la funzione di costo (loss):

1. **Approcci Pointwise**: Come nell'esempio lineare precedente, ogni singola coppia query-documento viene valutata a sé stante e le viene assegnato un punteggio. L'obiettivo del modello è predirre esattamente tale punteggio numerico, configurando il task come un puro problema di regressione o di classificazione multiclasse. Il grande limite di questi modelli è che ignorano totalmente la posizione finale che il documento assumerà all'interno della lista dei risultati.

2. **Approcci Pairwise**: In questa variante, l'unità di misura non è il singolo documento, ma una coppia di risultati. Al sistema vengono fornite delle preferenze espresse a coppie, indicando ad esempio che per una data query $q$, il documento $d_1$ è migliore del documento $d_2$. L'obiettivo del modello (spesso inquadrato come una classificazione binaria) è assegnare punteggi che preservino e rispettino questa gerarchia di preferenze. Un noto algoritmo pairwise è **RankNet**, che utilizza una funzione di perdita derivabile. Tuttavia, anche questo metodo non valuta l'effettiva rilevanza del documento all'interno della sua specifica posizione finale nella lista.

3. **Approcci Listwise**: Rappresentano la metodologia più olistica, in cui al sistema viene fornito il ranking ideale (perfetto) dell'intera lista dei risultati per ogni query. Lo scopo ultimo è massimizzare la qualità globale dell'elenco generato valutandolo nella sua interezza durante la fase di addestramento. Di conseguenza, l'obiettivo è ottimizzare direttamente le metriche di valutazione come l'**NDCG@K**. Purtroppo, l'impiego di queste logiche scontra con una forte barriera matematica: solitamente, non è possibile applicarvi in maniera diretta le tecniche di ottimizzazione standard basate sulle derivate, come lo **(Stochastic) Gradient Descent**.

### Il Modello Probabilistico BM25 e la Gestione dei Documenti

Spostandoci dai concetti puramente strutturali ai modelli matematici veri e propri, è fondamentale analizzare **Okapi BM25**, dove l'acronimo sta per "Best Matching" e Okapi è il nome del primo sistema IR ad aver implementato questa metrica. Il BM25 rappresenta lo stato dell'arte (**SOTA**) e si dimostra decisamente superiore al coseno puro per le rappresentazioni Bag-of-Words (**BOW**). Si tratta di una funzione probabilistica che si basa sull'ipotesi di indipendenza dei termini per approssimare la reale probabilità che un documento sia pertinente all'intento di ricerca.

La formula generale del BM25 è espressa come:

$$BM25(d,q) = \sum_{t} IDF_t \tau(F_t)$$

Questa equazione pondera attentamente sia la frequenza del termine che la lunghezza fisica del testo analizzato. La prima variabile, $IDF_t = log(N/n_t)$, rappresenta la frequenza inversa del documento (**inverse document frequency**) e diminuisce l'importanza dei termini troppo comuni. Il core della formula risiede tuttavia nella componente legata alla frequenza $F_t$:

$$F_t = \frac{f_{t,d}}{1 - b + b \cdot l_d / L}$$

In questo frangente, $f_{t,d}$ è la frequenza grezza del termine $t$ nel documento $d$, mentre al denominatore avviene un processo di **Pivoted length normalization** (normalizzazione imperniata sulla lunghezza). Qui, $l_d$ indica la lunghezza specifica del documento in esame e $L$ denota la lunghezza media dei documenti nell'intera collezione (**average doc length**, o **avdl**). Il parametro **$b$** ha lo scopo di stabilire quanta importanza dare alla penalizzazione della lunghezza. Questo perché i testi più lunghi contengono statisticamente più parole e avrebbero naturalmente un vantaggio ingiusto nel combinarsi con qualsiasi query. Se impostiamo $b > 0$, il denominatore agisce penalizzando i documenti che superano la media ($> avdl$) e premiando quelli più concisi ($< avdl$).

[![[Pasted image 20260417121437.png]]


Per mitigare ulteriormente l'impatto esplosivo di frequenze molto elevate per un singolo termine, il risultato $F_t$ non viene utilizzato puro, ma processato da una **funzione di saturazione** $\tau(F_t) = \frac{F_t}{k + F_t}$. Questa dinamica introduce una non-linearità fondamentale nel contributo della frequenza. Qualora non si volesse passare per un processo di ottimizzazione personalizzato, il BM25 prevede due iperparametri di default solitamente molto stabili: $k$ impostato tra 1.2 e 2, e $b = 0.75$.

![[Pasted image 20260417121516.png]]

### L'Estensione ai Campi Strutturati: Il Modello BM25F

I documenti complessi moderni (come articoli accademici o intere pagine web) raramente sono blocchi testuali monolitici; si presentano come artefatti ricchi di **campi strutturati** o multi-field (es. titolo, abstract, riassunto, autori per un paper; titolo, url, corpo della pagina, ancore per un sito web).

Per gestire questo grado di strutturazione, la formula originale è stata estesa nel modello **BM25F**, rappresentato come:

$$BM25F(d,q) = \sum_{t} IDF_t \tau(F_t)$$

$$F_t = \sum_{s} \frac{w_s \cdot f_{t,s}}{1 - b_s + b_s \cdot l_s / L_s}$$

In questa variante, le frequenze vengono calcolate a livello del singolo segmento semantico. La variabile $f_{t,s}$ rappresenta la frequenza del termine nel campo specifico $s$, valutata in relazione alla lunghezza di tale campo $l_s$ e alla media globale delle lunghezze per la stessa tipologia di campo
$L_s$. Rispetto al modello di base, viene introdotto un nuovo moltiplicatore cruciale: **$w_s$**, che assegna un peso variabile e definisce l'importanza relativa del campo $s$ rispetto agli altri. Questa estensione genera un'esplosione dei parametri da configurare. Mentre il BM25 classico possedeva solo 2 parametri liberi ($b$ e $k$), il BM25F richiede di gestire ben **$2S + 1$** parametri (dove $S$ indica il numero totale dei campi analizzati), ossia i pesi $w_s$, le penalizzazioni $b_s$ per ogni campo, più la costante di saturazione globale $k$. Questa mole di variabili rende impossibile una calibrazione manuale e costringe ad affidarsi proprio alle metodologie del **Learning to Rank** per individuare la combinazione ottimale.

### Sfide nell'Ottimizzazione Listwise e Tecniche Adottate

Considerando l'applicazione di algoritmi Machine Learning al tuning del BM25F, per prima cosa definiamo un dataset dove ogni query è associata a candidati annotati manualmente con etichette di rilevanza progressiva (da 0 a 4 stelle). Lo spazio delle ipotesi sarà composto da tutte le infinite e possibili variazioni delle funzioni BM25F. L'obiettivo valutativo è massimizzare una metrica listwise complessa, ad esempio la **Normalized Discounted Cumulative Gain (NDCG@10)**, la cui formula del gain cumulativo è:

$$DCG_p = \sum_{i=1}^{p} \frac{2^{rel_i} - 1}{log(1+i)}$$

Questa equazione scala l'importanza del documento in modo esponenziale rispetto alla sua etichetta di partenza: un documento con relevance 0 porta un incremento di 0 punti, mentre uno con punteggio massimo (4) inietta ben 17 punti di scarto ($2^4 - 1$).

Posti l'obiettivo di apprendere un modello $h$ (ovvero un modello BM25F governato dal set di parametri $\Theta$) per ordinare un set di documenti ($D = \{d_1, d_2, ...\}$), incontriamo un grave ostacolo teorico per gli approcci Listwise puri. metriche come l'NDCG dipendono intrinsecamente dalle posizioni finali occupate dai risultati e non dai semplici punteggi numerici calcolati. In altre parole, l'operazione di ordinamento globale (**sort $\{h(d_1), h(d_2), ...\}$**) gioca un ruolo centrale. Purtroppo, l'operazione di ordinamento (sort) non costituisce una funzione continua né derivabile. Non potendo calcolare un gradiente dell'ordinamento, l'ottimizzazione tramite Gradient Descent diviene strutturalmente inapplicabile in modo diretto.

Per bypassare questa difficoltà, l'Information Retrieval adotta vie traverse:

- Si minimizza una **loss pairwise proxy** (come il costo generato dall'algoritmo RankNet), pur sapendo che ottimizzare gli incroci documentali a coppie non equivale alla perfezione listwise.

- Si fa ricorso a soluzioni molto più efficaci, come il **Lambda-MART**, un algoritmo strutturato attorno a una funzione di costo modificata che riesce ad approssimare pseudo-gradienti utili per ottimizzare le metriche NDGC in via indiretta. Il MART, conosciuto anche come **Gradient Boosted Regression Tree (GBRT)**, utilizza insiemi di alberi di regressione, e librerie specializzate come **LightGBM** permettono di sfruttare oggi agilmente questa tecnologia.

![[Pasted image 20260417121826.png]]

Questa problematica evidenzia infatti il più grande difetto dell'approccio Pairwise puro applicato all'Information Retrieval. Questo metodo si impegna a minimizzare il volume totale delle coppie classificate scorrettamente nel sistema. Tuttavia, nel mondo reale e per ottimizzare correttamente l'NDCG, l'ordinamento accurato dei primi risultati mostrati (**top result pairs**) assume un'importanza enormemente superiore rispetto alla perfezione dell'ordine nelle posizioni periferiche o basse dell'elenco. Per tale motivo, quantificare genericamente le violazioni delle coppie di documenti senza pesarle posizionalmente non produrrà mai un indicatore affidabile per massimizzare le valutazioni NDCG destinate agli utenti.

### Glossario e Concetti Chiave

- **Term Proximity**: La metrica che valuta la vicinanza strutturale tra i vari termini della ricerca all'interno del corpo testuale della pagina, definendone la densità contestuale.

- **BM25 / BM25F**: Funzioni matematiche probabilistiche all'avanguardia basate sull'indipendenza dei termini; la versione F è specializzata nel gestire documenti segmentati in molteplici campi (title, abstract, test, ecc.), richiedendo un elevato numero di parametri da sintonizzare.

- **Pivoted Length Normalization**: Una specifica tecnica all'interno degli algoritmi di penalizzazione che sfavorisce proporzionalmente i documenti testuali eccessivamente lunghi per evitare che i loro ampi vocabolari "acchiappino" ingiustamente un punteggio elevato.

- **DCG / NDCG**: Sigla di (Normalized) Discounted Cumulative Gain, è una complessa metrica di valutazione Listwise che sfrutta potenze matematiche per premiare smisuratamente un documento eccellente posto nelle primissime posizioni della classifica, svalutando proporzionalmente i documenti utili se relegati in fondo alla pagina.

- **Lambda-MART / GBRT**: Sofisticati algoritmi di Machine Learning basati su alberi decisionali che riescono ad approssimare la massimizzazione dell'NDCG aggirando matematicamente il problema della non-derivabilità della funzione di ordinamento (sort).

---
