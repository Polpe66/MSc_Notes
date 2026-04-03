# Slide 1: L'Architettura Generale di un Motore di Ricerca

Per comprendere come valutare un sistema di Information Retrieval, è essenziale osservare la sua architettura interna, la quale si divide in componenti di elaborazione **Offline** e **Online**. La fase offline è propedeutica e dedicata alla preparazione dell'infrastruttura sui dati: partendo da una vasta **Document Collection**, il sistema esegue un processo di indicizzazione ("Indexing") per costruire un **Inverted Index**. Parallelamente, un processore estrae le caratteristiche dai testi (Feature Processor) per memorizzarle all'interno di una **Document Features Repository**. In questa fase viene anche effettuato il training di un modello di **Learning-to-rank**, sfruttando un insieme di dati di addestramento (Training Data). Durante la fase online, il sistema processa in tempo reale la **Query** immessa dall'utente: quest'ultima viene innanzitutto espansa e passata al modulo di Query Processing, che consulta l'indice invertito. I documenti candidati passano poi a una fase di calcolo e recupero delle feature (Feature Lookup and Computation), per essere infine ordinati in base alla loro pertinenza da una **Learned Ranking Function** e presentati all'utente.

[INSERIRE IMMAGINE: Diagramma di flusso dell'architettura del motore di ricerca divisa tra pipeline online e pipeline offline]

### Efficienza, Efficacia e la Misura della Soddisfazione

La valutazione di un motore di ricerca parte da interrogativi di natura prettamente prestazionale. Dal punto di vista dell'efficienza, ci si interroga sulla velocità con cui il sistema indicizza la collezione (in numero di documenti elaborati per ora) e sulla sua capacità di eseguire un'indicizzazione incrementale, ad esempio assorbendo 10.000 nuovi prodotti al giorno. Riguardo alla ricerca in sé, è fondamentale misurare la latenza e i requisiti di elaborazione della CPU necessari per elaborare query su indici di grandissime dimensioni, come collezioni da 5 milioni di documenti. Inoltre, si valuta la qualità dei servizi collaterali, verificando se il sistema è in grado di suggerire all'utente prodotti correlati validi per l'acquisto.

Questi aspetti tecnici, per quanto cruciali, non descrivono tuttavia l'efficacia e la qualità intrinseca del motore di ricerca in ottica utente. L'obiettivo finale di un sistema IR è garantire un'alta soddisfazione durante l'esperienza di ricerca, concentrandosi in particolar modo sulla bontà della pagina dei risultati (SERP) costruita per ogni specifica interrogazione. Valutare se un utente sia "felice" rappresenta però una sfida notevole. Un segnale apparente di successo è l'elevato numero di clic sui risultati restituiti, ma questo dato va analizzato criticamente poiché titoli o sommari fuorvianti potrebbero ingannare gli utenti e forzare clic non legati a una reale pertinenza. In determinate situazioni, l'assenza di clic ("no clicks") può persino configurarsi come una buona notizia se, per esempio, l'utente trova l'informazione desiderata direttamente nelle anteprime della SERP. Ulteriori segnali forti di soddisfazione derivano da azioni concrete post-ricerca, come l'acquisto di beni investendo ingenti somme di denaro, il ritorno di visitatori ricorrenti su base settimanale o mensile, e l'analisi del **Dwell time**, ovvero il tempo trascorso sulla pagina di destinazione dopo aver cliccato un link della SERP prima di tornare alla lista dei risultati.

### Misurare la Rilevanza: Il Metodo Cranfield e TREC

Essendo la felicità di natura elusiva e complessa da tracciare, il proxy più comune utilizzato nella valutazione accademica e industriale è la misurazione della **rilevanza dei risultati di ricerca**. Tale metodologia fu pionieristicamente introdotta da Cyril Cleverdon attraverso gli Esperimenti di Cranfield.

[INSERIRE IMMAGINE: Fotografia di Cyril Cleverdon, pioniere degli esperimenti di Cranfield]

La quantificazione formale della rilevanza richiede l'interazione di tre elementi imprescindibili: una collezione documentale di test (benchmark), una suite di query prefissate, e un giudizio formale che associ a ogni singola coppia query-documento un'etichetta di rilevanza o non-rilevanza. Supponendo di voler valutare un nuovo algoritmo, si può immaginare di incrociare una collezione da 5 milioni di documenti con un campione di 50.000 query, per produrre una matrice di giudizi. I giudizi possono manifestarsi nella forma più semplice come valutazioni binarie (rilevante contro non rilevante) o adottare misurazioni più sfumate su scale numeriche progressive (es. 0, 1, 2, 3...).

Questo approccio incontra limiti fisici colossali. Moltiplicando 5 milioni di documenti per 50.000 query si otterrebbero un quarto di trilione ($0.25 \times 10^{12}$) di coppie teoriche da valutare. Se per ogni giudizio un revisore umano impiegasse solo 2,5 secondi, occorrerebbero circa 170 milioni di ore-persona per completare il task. È pertanto obbligatorio restringere il numero dei documenti da valutare a un sottoinsieme della collezione totale. Per abbattere i costi elevati degli esaminatori esperti, la ricerca ha ampiamente studiato l'utilizzo del **crowdsourcing** tramite piattaforme online come Amazon Mechanical Turk, per far valutare i risultati a lavoratori generici a basso costo. Il principale insegnamento derivante dalla letteratura in merito è che questo metodo restituisce un segnale di base utile, ma la varianza qualitativa derivante dai giudizi è inevitabilmente molto alta.

### La Costruzione delle Query e le Collezioni Pubbliche

Per testare adeguatamente i sistemi, le test query impiegate devono essere direttamente pertinenti ai documenti a disposizione e, soprattutto, risultare rappresentative dei reali bisogni formativi di un utente. L'estrazione casuale di termini dai testi archiviati per simulare query è considerata una pessima pratica; un metodo decisamente migliore consiste invece nel campionare interrogazioni autentiche estrapolandole dai log del motore di ricerca. Nelle metodologie classiche sviluppate prima dell'avvento del Web, dove i tassi di interrogazione esigui limitavano la disponibilità dei log, la prassi richiedeva che gli esperti ideassero e confezionassero a mano i cosiddetti "bisogni dell'utente" (che la terminologia TREC chiama **topics**) e le query associate.

L'ecosistema dell'IR si basa fortemente su collezioni di test pubbliche. Di seguito un riepilogo tabellare storico dei benchmark, che mostra l'enorme salto di scala avvenuto con le raccolte TREC:

| **Collection** | **NDocs** | **NQrys** | **Size (MB)** | **Term/Doc** | **Q-D RelAss** |
| -------------- | --------- | --------- | ------------- | ------------ | -------------- |
| ADI            | 82        | 35        |               |              |                |
| AIT            | 2109      | 14        | 2             | 400          | >10,000        |
| CACM           | 3204      | 64        | 2             | 24.5         |                |
| CISI           | 1460      | 112       | 2             | 46.5         |                |
| Cranfield      | 1400      | 225       | 2             | 53.1         |                |
| LISA           | 5872      | 35        | 3             |              |                |
| Medline        | 1033      | 30        | 1             |              |                |
| NPL            | 11,429    | 93        | 3             |              |                |
| OSHMED         | 34,8566   | 106       | 400           | 250          | 16,140         |
| Reuters        | 21,578    | 672       | 28            | 131          |                |
| TREC           | 740,000   | 200       | 2000          | 89-3543      | >> 100,000     |
|                |           |           |               |              |                |

**TREC (Text Retrieval Conference)** è il punto di riferimento in questo ambito: un'iniziativa sponsorizzata dal National Institute of Standards and Technology (NIST) il cui fine primario è consolidare l'infrastruttura di test per valutazioni su vasta scala nel dominio IR, unificando sia la ricerca sui metodi che la creazione dei materiali. L'accezione di "Information Retrieval" viene qui mantenuta volutamente vasta, per abbracciare tutte le tecniche dedicate all'accesso a informazioni non strutturate preventivamente per le macchine. TREC è suddiviso concettualmente in **track**, vere e proprie aree di interesse ispirate da specifici "use case" e bisogni dell'utente.

Ogni collezione di test TREC si articola su tre direttrici: i documenti, i bisogni informativi o "topic" e, infine, i giudizi di pertinenza (relevance judgments), i quali descrivono quali testi andrebbero estratti per i determinati argomenti. Quando un sistema algoritmico processa un intero set di istruzioni all'interno di una collezione, il risultato finale si chiama **run**. Nelle prime edizioni, per risolvere il problema dell'impossibilità di valutare l'intera base di dati di TREC, venne ideata la tecnica del **Pooling**. Essa consiste nel fondere in un solo "pool" i documenti posizionati nelle primissime posizioni dai vari partecipanti; solo quelli all'interno del bacino vengono successivamente valutati dal giudizio umano. Ai fini del punteggio finale, qualunque documento posizionato fuori da questo limitato pool viene considerato di default come non rilevante. Numerose divisioni interne a TREC continuano tuttora la ricerca per ideare modalità ottimali e imparziali per il campionamento su larga scala.

### Bisogno Informativo e Valutazioni Binarie: Precision e Recall

Prima di passare al calcolo delle metriche di ranking vere e proprie, bisogna fare una distinzione categorica: la pertinenza finale valutata da un benchmark risponde all'effettivo bisogno informativo di fondo, e non alla stringa di testo digitata dall'utente. Per intenderci, se l'esigenza reale è "il fondo della mia piscina sta diventando nero e necessita pulizia", e l'utente digita semplicemente "pulitore piscina", un algoritmo deve soddisfare concettualmente la prima frase per risultare rilevante. Per questo motivo i vari partecipanti ai convegni TREC hanno generalmente la libertà di derivare la query dalle descrizioni dei topic in maniera autonoma, adoperando sia procedure manuali sia automazioni.

Assumendo che i giudizi di rilevanza siano esclusivamente binari (un documento o è pertinente, o non lo è), l'IR adotta storicamente due metriche non basate sull'ordinamento chiamate **Precision** e **Recall**.

- La **Precision** si definisce come la porzione dei documenti recuperati dal sistema che risultano concretamente pertinenti all'interno della ristretta cerchia di testi proposti al lettore. In termini probabilistici: $P(\text{relevant retrieved} \mid \text{retrieved})$.

- La **Recall** valuta la metrica dal punto di vista globale, calcolando la percentuale di tutti i documenti effettivamente pertinenti celati nel corpus che l'algoritmo ha saputo intercettare: $P(\text{relevant retrieved} \mid \text{relevant})$.

Questi due concetti vengono estrapolati quantificando i Veri Positivi ($tp$), Falsi Positivi ($fp$), e Falsi Negativi ($fn$):

|                                | **Rilevante (Relevant)** | **Non Rilevante (Nonrelevant)** |
| ------------------------------ | ------------------------ | ------------------------------- |
| Recuperato (Retrieved)         | $tp$                     | $fp$                            |
| Non Recuperato (Not Retrieved) | $fn$                     | $tn$                            |
|                                |                          |                                 |

Applicando questa matrice, possiamo quantificare aritmeticamente le misure: $precision = \frac{tp}{tp+fp}$ ovvero $precision = \frac{\text{Number of relevant documents retrieved}}{\text{Total number of documents retrieved}}$. $recall = \frac{tp}{tp+fn}$ ovvero $recall = \frac{\text{Number of relevant documents retrieved}}{\text{Total number of relevant documents}}$.

### L'Armonizzazione con la F-Measure (o F-Score)

Spesso durante la valutazione emerge la difficoltà intrinseca di ottimizzare simultaneamente i parametri di Recall e Precision. Si adotta perciò una combinazione singola definita **F-Measure** (o F-Score), la quale restituisce il loro valore medio calcolato però tramite media armonica, anziché aritmetica o geometrica.
La formula di base è: $F = \frac{1}{0.5\frac{1}{P} + 0.5\frac{1}{R}} = 2\frac{PR}{P+R}$

Il motivo matematico alla base di questa scelta risiede nel fatto che la media armonica fornisce un risultato che non potrà mai superare né la media aritmetica né quella geometrica e, soprattutto, quando i due valori a confronto presentano grandi deviazioni, tende ad avvicinarsi fortemente verso il numero più basso dei due. Se, come puro caso teorico, un motore di ricerca cattura tutti i documenti possibili portando il valore Recall al 100%, la sua Precision subirà un inevitabile crollo; il valore di F-Score si adatterà assecondando e mostrando con precisione le penalizzazioni dovute al valore peggiore.

Nei casi in cui il progettista intenda dare priorità mirata ad uno solo dei due indicatori asimmetrici, può fare affidamento su una formula di ponderazione definendo l'iperparametro $\beta$ attraverso l'equazione $\alpha = 1/(1+\beta^{2})$ che conferisce il peso $\alpha$ alla Precision e il suo complimento alla Recall. La **F-Measure pesata** si scriverà dunque in questo modo: $F_{\beta} = \frac{1}{\alpha\frac{1}{P} + (1-\alpha)\frac{1}{R}} = (\beta^{2}+1)\frac{PR}{\beta^{2}P+R}$ Ne consegue che, abbassando la frazione in modo che $\beta < 1$, si avvantaggerà lo studio e il peso della Precision (andandone in direzione convergente); mentre se si solleverà l'asticella cosicché $\beta > 1$, la priorità dell'equazione ricadrà forzatamente sulla Recall.

---

### Glossario e Concetti Chiave

- **Information Need vs Query**: Distinzione critica fra l'effettivo bisogno conoscitivo, di senso compiuto, dell'utente e le parole chiave semplificate che vengono infine digitate nell'interfaccia. La valutazione della rilevanza si aggancia metodologicamente al primo concetto.

- **Relevance e Benchmark**: La pertinenza dei risultati ottenuti, che costituisce la proxy fondamentale della qualità. Viene analizzata incrociando Document Collection e Topic Query predefinite contro giudizi di pertinenza emessi (solitamente per via di meccanismi come il *Pooling* e il Crowdsourcing).

- **Precision e Recall**: Le metriche fondanti relative a valutazioni binarie nei documenti recuperati dal software, le quali quantificano l'efficacia misurando la frazione di utilità ritornata (Precision) e la completezza delle fonti raccolte (Recall).

- **F-Measure**: Media armonica che sintetizza Precision e Recall, progettata matematicamente per avvicinarsi e mettere in rilievo la stima minima, impedendo a variazioni eccessive di uno dei due poli di ingannare il voto prestazionale finale.

---

### Misure Basate sull'Ordinamento (Rank-Based Measures)

Una volta compresi i fondamenti della rilevanza binaria aspecifica (come Precision e Recall), è necessario introdurre le metriche basate sull'ordinamento, fondamentali poiché in un motore di ricerca reale la posizione in cui appare un risultato è cruciale. Queste misure si dividono in due macro-categorie: quelle che operano ancora in un regime di rilevanza binaria, come la **Mean Average Precision (MAP)**, la **Precision@K** e il **Mean Reciprocal Rank (MRR)**, e quelle capaci di gestire molteplici livelli di rilevanza sfumata, come il **Normalized Discounted Cumulative Gain (NDCG)**.

### Mean Average Precision (MAP)

Per valutare la qualità dell'intero ranking restituito per una singola query, si utilizza l'**Average Precision (AP)**. Questa misura considera la posizione in classifica di ogni singolo documento pertinente man mano che la recall aumenta lungo la lista dei risultati. Più precisamente, si calcola la "Precision@K" esclusivamente nei punti $K_{1}, K_{2}, \dots, K_{R}$ in cui viene effettivamente incontrato un documento utile. Ad esempio, per una query che possiede in totale $R=3$ documenti rilevanti, i quali compaiono alle posizioni 1, 3 e 5 della classifica, l'AP si calcola sommando le precisioni in quei punti e dividendole per 3, ottenendo $\frac{1}{3}\cdot(\frac{1}{1}+\frac{2}{3}+\frac{3}{5})\approx0.76$.

Analizzando casi più estesi, se un sistema restituisce due differenti ordinamenti (Ranking #1 e Ranking #2) per la stessa serie di documenti rilevanti, l'Average Precision premia nettamente l'algoritmo che posiziona i testi pertinenti più in alto. Calcolando l'AP su una lista ipotetica, il Ranking #1 potrebbe ottenere un punteggio di $(1.0+0.67+0.75+0.8+0.83+0.6)/6=0.78$, mentre un peggiore Ranking #2 si fermerebbe a $(0.5+0.4+0.5+0.57+0.56+0.6)/6=0.52$.

[INSERIRE IMMAGINE: Esempio visivo del calcolo della Mean Average Precision con due ranking di documenti, rappresentati da fogli grigi (rilevanti) e bianchi (non rilevanti)]

La **MAP (Mean Average Precision)** non è altro che la media aritmetica di tutti i valori di AP calcolati trasversalmente su un intero set di query, fornendo così un singolo valore riassuntivo della qualità del sistema. Questo approccio adotta un meccanismo di macro-averaging, il che significa che ogni query pesa equamente sul risultato finale, indipendentemente dal fatto che per alcuni bisogni informativi esistano moltissimi documenti rilevanti e per altri pochissimi. Proprio per questa sua robustezza, la MAP rappresenta una delle misure più adoperate all'interno delle pubblicazioni scientifiche che trattano la rilevanza binaria.

### Precision@K e MAP@K nel Contesto Web

Ci si potrebbe chiedere se metriche olistiche come la MAP siano ottimali anche per la Web Search moderna. La risposta è che esse considerano la precisione a tutti i livelli di recall, ma sul web il numero totale di documenti pertinenti per una determinata query è spesso del tutto ignoto o potenzialmente sterminato. Ciò che conta davvero per un utente reale è quanti buoni risultati sono presenti nella primissima pagina (o nelle prime tre), ossia tra i primi 10 o 30 link restituiti.

Per catturare questa dinamica si introduce la **Precision@K (P@K)**, che fissa una soglia di ranking $K$ e calcola semplicemente la percentuale di documenti pertinenti presenti nei primi $K$ risultati, ignorando tutto ciò che si trova al di sotto di tale soglia. Ad esempio, se tra i primi tre risultati ne abbiamo due rilevanti, la Prec@3 sarà $2/3=0.66$; se il quarto risultato è irrilevante, la Prec@4 scenderà a $2/4=0.5$, dimostrando come questa curva non sia strettamente monotona crescente (infatti Prec@4 è inferiore a Prec@3). Se il quinto è nuovamente rilevante, la Prec@5 risalirà a $3/5=0.6$. Analogamente si può calcolare la Recall@K.

Il difetto principale della P@K è che non restituisce medie affidabili su un insieme eterogeneo di query, poiché il numero totale di documenti rilevanti specifici per ogni interrogazione influenza pesantemente i risultati. Per arginare il problema pur concentrandosi sui vertici delle classifiche, si ricorre alle metriche **AP@K** e **MAP@K**, ampiamente utilizzate nei Motori di Ricerca Web e nei Recommender Systems.
La formula per l'Average Precision al rango K per una determinata query $q_i$ è: $AP@K(q_{i})=\frac{1}{K_{i}}\sum_{k=1}^{K}P@k(q_{i})\cdot rel(q_{i},k)$ Dove $rel(q_{i},k)$ vale $1$ se l'elemento al k-esimo rango è rilevante, altrimenti $0$, e il fattore di normalizzazione è $K_{i}=\sum_{k=1}^{K}rel(q_{i},k)$.
Di conseguenza, la MAP@K sull'intero insieme di query $Q$ si ottiene calcolando la media aritmetica delle singole AP@K: $MAP@K(Q)=\frac{1}{|Q|}\sum_{q_{i}\in Q}AP@K(q_{i})$.

### Mean Reciprocal Rank (MRR)

Esistono scenari limite, molto frequenti sul Web, in cui per l'utente esiste un solo, unico documento rilevante. Questo accade nelle ricerche di un item specifico già noto ("known-item search"), nelle query navigazionali (cercare l'homepage di una determinata banca) o nella ricerca di un fatto o dato puntuale. In questi casi limite, la durata della ricerca per l'utente è direttamente proporzionale al rango occupato dalla risposta corretta: la posizione in classifica misura direttamente lo sforzo cognitivo e temporale impiegato.

Per misurare formalmente questo sforzo si utilizza il **Mean Reciprocal Rank (MRR)**, che generalizza il concetto basandosi sulla posizione in classifica $rank_i$ del *primo* documento utile restituito per la query $q_i$. Questo può coincidere banalmente con l'unico documento cliccato. Il Reciprocal Rank (RR) di una singola query è il reciproco della sua posizione ($1/rank_i$). L'MRR si ottiene facendo la media dei punteggi RR su tutto l'insieme di query: $MRR=\frac{1}{|Q|}\sum_{i=1}^{|Q|}\frac{1}{rank_{i}}$.

### Oltre la Rilevanza Binaria: Il Discounted Cumulative Gain (DCG)

Nei moderni motori di ricerca commerciali, i documenti non sono semplicemente utili o inutili, ma possiedono sfumature di utilità.

[INSERIRE IMMAGINE: Screenshot di una pagina dei risultati di ricerca (SERP) di Google per la query "information retrieval unipi", mostrando risultati istituzionali misti a informazioni generali]

Per gestire questa gradualità, la metrica più diffusa è il **Discounted Cumulative Gain (DCG)**. Essa poggia su due assunti cognitivi fondamentali: in primo luogo, i documenti altamente pertinenti sono intrinsecamente più utili rispetto a quelli marginalmente pertinenti; in secondo luogo, più un documento rilevante scivola in basso nella classifica, meno probabilità avrà di essere esaminato, risultando di conseguenza meno utile.

Il DCG accumula il guadagno o utilità ("gain") scendendo lungo le posizioni della classifica, applicando però una "penalità" progressiva ai ranghi più bassi (lo sconto, o "discount"). Svolgendo giudizi di pertinenza su una scala che va da $0$ a $m$ (con $m>2$), il semplice Cumulative Gain (CG) al rango $n$ sarebbe una pura somma: $CG=r_{1}+r_{2}+\dots+r_{n}$. Il DCG interviene penalizzando i documenti che compaiono ai ranghi maggiori di 1, dividendone il valore di rilevanza generalmente per il logaritmo della loro posizione: $1/log(rank)$. Usando la base 2, ad esempio, lo sconto applicato alla quarta posizione dimezzerà il punteggio ($1/log_2(4) = 1/2$), mentre all'ottava lo ridurrà a un terzo ($1/log_2(8) = 1/3$).
La formulazione standard al rango $p$ è: $DCG_{p}=rel_{1}+\sum_{i=2}^{p}\frac{rel_{i}}{log_{2}i}$.

Esiste tuttavia una formulazione alternativa, adottata da alcune grandi aziende di web search, che amplifica esponenzialmente il peso dei documenti altamente rilevanti per dare massima priorità al loro recupero: $DCG_{p}=\sum_{i=1}^{p}\frac{2^{rel_{i}}-1}{log(1+i)}$.

Ad esempio, valutando 10 documenti con giudizi su una scala da 0 a 3 (es: 3, 2, 3, 0, 0, 1, 2, 2, 3, 0), il DCG applicherà i logaritmi ai denominatori ottenendo valori parziali scontati, per poi sommarli progressivamente e giungere a un DCG complessivo al decimo rango pari a 9.61.

### Normalizzazione dei Punteggi: NDCG

Il limite del puro DCG risiede nell'impossibilità di fare confronti coerenti tra query che posseggono quantità di documenti rilevanti intrinsecamente differenti. Per risolvere questo problema strutturale si introduce il **Normalized Discounted Cumulative Gain (NDCG)**, diventato oggi uno standard assoluto nella valutazione della Web Search.

Il processo consiste nel calcolare prima il ranking ideale (chiamato Ideal DCG), ordinando i risultati decrescenti partendo dai giudizi di rilevanza più alti verso i più bassi. Il valore finale si ottiene dividendo il DCG misurato empiricamente per l'Ideal DCG calcolato teoricamente. A causa di questa normalizzazione, il valore dell'NDCG in qualsiasi posizione $p$ sarà sempre compreso tra 0 e 1 ($NDCG \le 1$). Riprendendo l'esempio precedente, se il ranking perfetto dei documenti avrebbe prodotto un Ideal DCG finale di 10.88, e il nostro algoritmo si è fermato a 9.61, l'NDCG risultante sarà pari a 0.88.

Di seguito una tabella riassuntiva che mostra il confronto diretto tra il Ground Truth (verità di base) e le funzioni di ranking algoritmiche (RF1 e RF2) su una scala ristretta a 4 documenti:

| **i** | **Ground Truth (Document Order)** | **ri​** | **Ranking Function 1 (Document Order)** | **ri​** | **Ranking Function 2 (Document Order)** | **ri​** |
| ----- | --------------------------------- | ------- | --------------------------------------- | ------- | --------------------------------------- | ------- |
| 1     | d4                                | 2       | d3                                      | 2       | d3                                      | 2       |
| 2     | d3                                | 2       | d4                                      | 2       | d2                                      | 1       |
| 3     | d2                                | 1       | d2                                      | 1       | d4                                      | 2       |
| 4     | d1                                | 0       | d1                                      | 0       | d1                                      | 0       |
|       | **$NDCG_{GT}=1.00$**              |         | **$NDCG_{RF1}=1.00$**                   |         | **$NDCG_{RF2}=0.9203$**                 |         |

Come si evince, il $MaxDCG$ (cioè l'Ideal DCG del Ground Truth) è pari a $4.6309$. La Ranking Function 1, seppur invertendo l'ordine dei due documenti con rilevanza massima 2 (d4 e d3), ottiene anch'essa un $DCG$ perfetto di $4.6309$, e quindi un NDCG di $1.00$. La Ranking Function 2, invece, inserendo un documento meno rilevante al rango 2, vede il suo punteggio abbassarsi a $4.2619$, ottenendo di conseguenza un NDCG normalizzato di $0.9203$.

### Ricapitolazione

In sintesi, i benchmark per la valutazione in ambito Information Retrieval necessitano strutturalmente di una Document collection, un Query set (i topic), e una Assessment methodology chiara. Questa metodologia può affidarsi a revisori umani, all'analisi dei clic degli utenti, o a sistemi ibridi. I giudizi estratti vengono infine quantizzati matematicamente all'interno di misure di bontà (come Precision o NDCG), le quali permettono alla comunità scientifica e industriale di confrontare le prestazioni di differenti motori e algoritmi su un terreno empirico unificato e standardizzato.

---

### Glossario e Concetti Chiave

- **Mean Average Precision (MAP)**: Metrica riassuntiva che calcola la media aritmetica dell'Average Precision su un insieme di query, adatta per rilevanze binarie. Ha la caratteristica di pesare equamente tutte le query analizzate (macro-averaging).

- **Precision@K e MAP@K**: Varianti delle metriche classiche ritagliate sulle necessità dei Motori di Web Search, focalizzandosi esclusivamente sulla densità di risultati utili all'interno di una determinata soglia $K$ (come i primi 10 risultati su schermo), ignorando le code di basso rango.

- **Mean Reciprocal Rank (MRR)**: Misura dello sforzo utente, si applica prevalentemente su scenari dove esiste o interessa un unico risultato corretto. Dipende direttamente dal reciproco della posizione in classifica del primo documento trovato.

- **Discounted Cumulative Gain (DCG)**: Metrica avanzata che abbandona la logica binaria, introducendo scale di rilevanza graduali. Il suo assunto logico penalizza matematicamente tramite un abbattimento logaritmico l'utilità di un testo man mano che esso viene scalzato in fondo ai risultati.

- **Normalized DCG (NDCG)**: Correttivo del DCG nato per rendere compatibili tra loro query eterogenee. Costringe l'esito frazionando il risultato reale per un "Ideal DCG" teorico, calcolato supponendo di aver ordinato i risultati dal più al meno influente.

---

# Slide 2: Introduzione al Linguaggio Naturale e alle sue Applicazioni

Il presente capitolo introduce i concetti fondanti legati al processamento delle informazioni testuali, definendo cos'è il linguaggio naturale, esplorando le enormi difficoltà intrinseche nella sua interpretazione automatica e illustrando come l'elaborazione del testo si interfacci con altri tipi di dati nelle applicazioni del mondo reale.

### Il Linguaggio Naturale: Caratteristiche e Complessità

Il **linguaggio naturale** è il mezzo che gli esseri umani usano quotidianamente per comunicare tra loro, sia in forma scritta che parlata, senza la necessità di ricorrere a formalismi artificiali predefiniti. A differenza dei linguaggi formali, come possono essere quelli matematici o i linguaggi di programmazione, si definisce "naturale" proprio perché si è evoluto in modo del tutto spontaneo. Esso non si limita alla semplice emissione di singole parole, ma si articola in frasi, interi discorsi, intonazioni e possiede un profondo contesto culturale. Di conseguenza, l'utilizzo di un linguaggio così articolato rappresenta un tratto distintivo ed esclusivo della specie umana. Due importanti studi accademici che inquadrano teoricamente questo fenomeno sono l'opera di James R. Hurford sull'unicità umana, i simboli appresi e il pensiero ricorsivo, e lo studio di Scott-Phillips e Blythe che indaga il motivo per cui la comunicazione combinatoria sia rara nel mondo naturale e il perché il linguaggio rappresenti un'eccezione a questa tendenza.

L'elaborazione di questo strumento comunicativo è estremamente complessa a causa di numerose sue caratteristiche intrinseche. Innanzitutto, il linguaggio si basa su **migliaia di simboli** e possiede una **sintassi complessa**.

[INSERIRE IMMAGINE: Rappresentazione di alberi sintattici e grafi delle dipendenze, ad esempio la scomposizione della frase "the angry bear chased the frightened little squirrel" e l'analisi di mercato legata ad Apple].

Inoltre, la semantica del linguaggio è prevalentemente **composizionale**, il che significa che il significato generale di un'espressione deriva solitamente dall'unione delle sue singole parti. Tuttavia, in questo ambito esistono varie sfumature che complicano l'analisi. Si passa da espressioni puramente composizionali (come "comprare un'auto" o "leggere un libro" ) alle collocazioni, ovvero associazioni abituali di parole (come "make the bed" o "saltare la lezione" ), per giungere alle **espressioni idiomatiche**, il cui significato non è in alcun modo deducibile in via letterale dalle singole parole (ad esempio, l'augurio "break a leg" o il proverbio "dalla padella nella brace" ).

Una delle sfide più gravose per un sistema automatizzato è la natura **potenzialmente ambigua** del testo. Questa ambiguità si presenta a molteplici livelli strutturali. Esiste l'ambiguità grammaticale (part of speech ambiguity), dove una medesima parola può rivestire ruoli sintattici differenti; per esempio, la parola "beat" può fungere sia da sostantivo ("Ascolta questo bel beat") sia da verbo ("Ti batterò a dama") . Sussiste poi un'ambiguità semantica legata al senso della parola (word sense ambiguity), come nel caso del termine "interest", che può indicare l'attenzione verso un argomento, oppure il tasso di interesse economico alzato da una banca . Infine, vi è l'ambiguità sintattica pura, in cui la struttura della frase permette interpretazioni multiple e contrastanti, ben esemplificata dalla nota gag in cui si afferma di aver sparato a un elefante mentre si indossava un pigiama .

Come se non bastasse, la comprensione reale si affida in maniera vitale alla **conoscenza condivisa del mondo** (shared world knowledge). Frasi apparentemente semplici nascondono significati che diamo per scontati: affermare "Sono più veloce di Lewis Hamilton" richiede all'ascoltatore di sapere chi sia costui per dedurre la relazione di velocità alla guida. Distinguere la natura di una connessione tra le affermazioni "Alexis e Kate sono sorelle" e "Alexis e Kate sono madri" implica comprendere che la prima definisce una relazione tra le due, mentre la seconda indica la medesima proprietà senza che via sia una diretta relazione madre-figlia tra loro. Il nostro bagaglio culturale ci consente di invalidare all'istante affermazioni logicamente impossibili come "Mia madre è più giovane di me", o di ricostruire il contesto fisico implicito di un'azione. Ad esempio, dire "Ero su una bici con un casco" implica naturalmente che il casco fosse sulla nostra testa, mentre dire "Ero su una bici con un motore elettrico" implica che il motore fosse un componente strutturale del mezzo .

Infine, il linguaggio è un'entità dinamica che viene **imparata da zero** durante la nostra vita e che **continua a evolversi** assieme a noi e alla società. Il vocabolario si adatta ai tempi, introducendo nuovi concetti o modificandone il senso: termini storici o scientifici come "aeroplano", "quark", "automobile" e "genocidio", o nomi divenuti brand universali come "Tesla", affiancano espressioni un tempo impensabili ma oggi di uso comune, come "L'ho googlato", "è nel cloud", "crowdfunded", "navigare" (in senso digitale) o termini emersi prepotentemente con recenti contesti storici, quali "super spreader" e "indossare una mascherina" .

### Natural Language Understanding (NLU) e Applicazioni Pratiche

Il campo della **Natural Language Understanding (NLU)** mira specificamente a costruire macchine che siano in grado di ricevere e fornire informazioni utilizzando il linguaggio naturale, emulando il modo in cui lo fanno gli esseri umani . Dal punto di vista computazionale e teorico, l'NLU è considerato un problema **AI-completo**, ovvero uno degli ostacoli più ardui dell'Intelligenza Artificiale, la cui risoluzione equivarrebbe a produrre un'intelligenza di livello umano. Pertanto, tutti i compiti pratici di *text analytics* che utilizziamo oggi sono, di fatto, delle necessarie semplificazioni del NLU ideate per rendere i problemi parzialmente o settorialmente risolvibili in modo agevole.

Le applicazioni pratiche derivanti da queste semplificazioni sono ormai onnipresenti nei nostri strumenti digitali. Esse alimentano motori di ricerca e assistenti virtuali (come Siri) in grado di estrarre e sintetizzare frammenti di testo web per rispondere a domande precise come "Quanto tempo ci vuole per arrivare sulla luna?", "Pioverà domani a Tbilisi?" o di interagire con richieste creative quali "Mi racconti una filastrocca" . Vengono impiegate massicciamente per l'aggregazione di notizie in profondità, permettendo di raggruppare sotto lo stesso cappello semantico articoli di diverse testate relativi allo stesso evento (ad esempio, le difficoltà di mercato di Volkswagen) . Inoltre, monitorano costantemente i flussi dei social network per identificare le tendenze del momento ("Trending topics") in vari paesi come Italia, Spagna o Giappone . Ulteriori declinazioni includono i filtri intelligenti delle caselle email, come l'Inbox di Gmail, che smista autonomamente la posta in contenitori predefiniti come Promozioni, Aggiornamenti o Spam , fino ad arrivare ai sofisticati strumenti di traduzione automatica capaci di rilevare la lingua di origine e tradurre intere comunicazioni istituzionali .

### Relazioni con Altri Tipi di Dati e Cross-media Processing

Le metodologie applicate al testo non restano confinate alle sole sequenze di parole, ma stringono fortissime relazioni con altre tipologie di dati.

Quando analizziamo i **Dati Tabulari** — i quali contengono valori numerici, categorici o simbolici organizzati con vincoli e strutture esplicite — scopriamo che è possibile estrarre informazioni strutturate direttamente da fonti testuali storiche, abilitando operazioni di *data mining* su informazioni originariamente non progettate per tali processi automatizzati . Un affascinante ponte tra i due mondi è rappresentato dall'uso di domande formulate in linguaggio naturale per interrogare veri e propri database strutturati.

| **Esempio di Interrogazione da Linguaggio Naturale a Tabella**                                            |
| --------------------------------------------------------------------------------------------------------- |
| **Tabella di contesto:** Classifica di lottatori (Nome, Numero di Regni, Giorni Combinati)                |
| **Domanda NLU:** Quale lottatore ha avuto il maggior numero di regni? -> **Risposta estratta:** Ric Flair |
| **Domanda NLU:** Qual è il numero di regni per Harley Race? -> **Risposta estratta:** 7                   |

Anche le reti complesse, o **Grafi**, tipiche dei Social Network, beneficiano ampiamente dell'elaborazione del linguaggio. Tali reti sono immense fonti aggiornate su gruppi, eventi e dibattiti. I metodi basati sulla mera analisi dei grafi consentono di mappare relazioni locali, macro-comunità e comportamenti su larga scala. Ciononostante, arricchire l'analisi strutturale del grafo con l'analisi del contenuto effettivo testuale condiviso, consente di caratterizzare minuziosamente gli elementi della rete, conferendo un significato dettagliato alle interazioni e facilitando l'indagine su processi complessi quali comportamenti anomali o tentativi di manipolazione.

Infine, l'intersezione tra elaborazione linguistica, **Immagini e Video** genera la branca del *Cross-media processing*. Un classico compito è l'**Image Captioning**, ovvero la generazione automatica di descrizioni testuali per il contenuto di un'immagine. Tali metodologie sono utilissime per affinare i processi di recupero e ricerca visiva e forniscono un aiuto inestimabile per le persone affette da disabilità visive, descrivendo loro in linguaggio naturale, e con vari livelli di accuratezza, le scene presentate a schermo.

[INSERIRE IMMAGINE: Griglia di esempi di "Image Captioning", che mostra valutazioni dalle descrizioni perfette senza errori a quelle vagamente correlate o completamente scollegate dall'immagine].

---

### Concetti Chiave del Capitolo

- **Linguaggio Naturale:** Il mezzo complesso e spontaneo, ricco di sfaccettature contestuali, mediante il quale gli esseri umani comunicano, distinguendosi radicalmente dai linguaggi formali.

- **Composizionalità ed Ambiguità:** La proprietà semantica per cui il significato totale deriva solitamente dalle singole parti del discorso, limitata però dalle espressioni idiomatiche e dalla triplice forma di ambiguità (grammaticale, semantica, sintattica).

- **Problema AI-Completo:** Un livello di difficoltà computazionale associato alla Natural Language Understanding, la cui risoluzione definitiva corrisponderebbe alla creazione di un'intelligenza artificiale pari a quella umana.

- **Cross-media Processing:** L'applicazione di tecniche di comprensione del testo su formati dati differenti, capace di convertire e unire l'informazione proveniente da tabelle, reti e immagini (es. Image Captioning).

---

### Interazioni Visive e Tecnologie Fondamentali

Il *cross-media processing* si spinge fino alla generazione di rappresentazioni visive astratte a partire dal testo. Questo approccio abilita metodi di recupero delle informazioni (*retrieval*) molto più intelligenti e sofisticati. Attraverso l'uso di modelli informatici denominati VisSim, VisReg e Text2Vis, è possibile tradurre stringhe testuali descrittive in precise interpretazioni visive, confrontandole con l'azione scenica reale . Ne è un esempio l'elaborazione della frase "un uomo si prepara a lanciare un frisbee" ("a man gets ready to throw a frisbee"), dove il modello isola l'azione e la relaziona all'immagine.

[INSERIRE IMMAGINE: Sequenza di fotogrammi video che mostrano individui che giocano a frisbee, affiancati verticalmente dalle diciture VisSim, VisReg e Text2Vis in risposta all'input testuale corrispondente].

Per raggiungere questi traguardi ci si avvale di una profonda convergenza di diverse **Tecnologie**. Storicamente e metodologicamente, le discipline del **Natural Language Processing (NLP)**, dell'**Information Retrieval (IR)** e del **Machine Learning (ML)** non sono nettamente distinte tra loro . Al contrario, si influenzano e si nutrono reciprocamente: i metodi legati al NLP sono molto spesso costruiti sfruttando e poggiandosi sui fondamenti dell'IR e del ML. Allo stesso tempo, il ML adotta sistematicamente le metriche e le misure tipiche dell'IR per poter definire gli obiettivi qualitativi dei propri modelli di apprendimento. Di conseguenza, il ML presuppone in modo intrinseco che il linguaggio naturale debba essere prima manipolato e strutturato dai metodi NLP e IR affinché gli algoritmi predittivi possano lavorarci efficacemente.

[INSERIRE IMMAGINE: Diagramma a tre cerchi interconnessi da frecce che rappresentano le aree NLP, IR e ML, indicando la loro natura fluida e interdipendente].

### Il Concetto di Pipeline ed Estrazione dei Dati dal Web

Questo indispensabile pre-processamento è strutturato attraverso le cosiddette **Pipelines**. Una *Processing Pipeline* si definisce come una catena ordinata di passaggi o moduli sequenziali attraverso cui un dato passa per essere trasformato, analizzato o elaborato . Questo concetto è ricorrente nei metodi di *text analytics*, in quanto il testo grezzo è raramente elaborabile "così com'è" dai complessi metodi di Machine Learning. Esso richiede tassativamente di essere trasformato in una rappresentazione che si adatti al metodo specifico in uso in quel momento. È fondamentale notare che queste trasformazioni non sono mai delle semplici conversioni di formato informatico: esse possono filtrare attivamente e modificare l'informazione per far sì che venga sfruttata al meglio dagli stadi successivi della pipeline, avendo come obiettivo il sostanziale miglioramento del risultato finale dell'intero processo.

Il primo step empirico di questa catena riguarda spesso l'ottenimento e l'estrazione di testo direttamente dalla rete. Nel linguaggio di programmazione Python, si utilizzano librerie specifiche per estrapolare agilmente testo o dati da siti e applicazioni web. Il primo pacchetto citato in questo contesto è **urllib**, il quale implementa metodi che consentono una necessaria interazione a basso livello direttamente con i server web ospitanti . Il recupero del contenuto di una pagina risulta tipicamente semplice: importando il modulo `request` da `urllib`, è possibile definire l'URL desiderato (ad esempio la directory `http://hpc.isti.cnr.it/~nardini/`), utilizzare la funzione `urlopen` per ottenere la risposta dal server e infine leggerla e decodificarla nel formato UTF-8, facendosi restituire un output grezzo corrispondente al codice sorgente HTML (`<!DOCTYPE html>\n<html lang="en-US">...`) .

Tuttavia, tale codice include marcatori e strutture superflue. Per estrarre testo "pulito", si adotta il pacchetto **BeautifulSoup**. Questa libreria implementa svariati metodi progettati per navigare, modificare ed estrarre sistematicamente dati in formato HTML e XML, permettendo all'utente di isolare unicamente le informazioni di reale interesse all'interno di una determinata pagina web . Operativamente, una volta ottenuta la risposta decodificata con `urllib`, il codice HTML viene passato al costruttore `BeautifulSoup` assieme al nome del parser (come ad esempio "html5lib") . A questo punto, per recuperare le informazioni si può usare un metodo rapido e basilare come `soup.get_text()` per estrarre in blocco tutto il testo della pagina, oppure adottare un approccio che offre un controllo maggiore, come una *list comprehension* (`[''.join(s.findAll(text=True)) for s in soup.findAll('p')]`) per cercare, unire ed estrarre iterativamente solo il puro testo contenuto in ogni singolo tag di paragrafo (`p`) .

### Introduzione a NLTK e Prime Analisi sul Testo

Con il testo purificato, la pipeline si avvale di librerie specializzate come **NLTK** (Natural Language Toolkit) . Si tratta di una potente e rinomata libreria *open source* che supporta e velocizza enormemente lo sviluppo rapido di applicazioni in ambito NLP, offrendo validi strumenti sia per l'elaborazione di tipo simbolico sia per quella squisitamente statistica. La documentazione di NLTK include un libro online che fornisce numerosi esempi guidati per affrontare svariati compiti pratici. La sua inizializzazione prevede alcuni tipici passi: l'installazione nel proprio ambiente virtuale (es. `conda install nltk`), la sua importazione nel codice (`import nltk`) e l'esecuzione di un gestore di download interattivo (`nltk.download()`), utile per poter scegliere e scaricare i pacchetti di dati linguistici più popolari e indispensabili .

A questo punto si entra nel vivo della scomposizione della frase, focalizzandosi su **Tokens & Sentences**. Una delle operazioni più elementari, ma al contempo centrali sul testo, consiste nell'isolare e identificare le singole parole che lo compongono, unità che assumono il nome tecnico di **token**. In informatica, questo processo assume il nome di **Tokenizzazione**. In NLTK è possibile suddividere il documento nei suoi rispettivi token molto agevolmente. La libreria fornisce allo scopo una funzione basilare (`word_tokenize`) che esegue questa segmentazione in modo consapevole rispetto alle regole intrinseche della lingua . Importando questa funzione e applicandola a una variabile di testo, essa restituirà una lista segmentata . Testando la funzione su un estratto inglese, essa converte la stringa di partenza frammentandola in elementi isolati ('with', 'you', ',', 'no', 'longer', 'as', 'you', 'call', 'and', 'my', 'faithful', 'slave', 'yourself'), separando dunque non solo le parole, ma anche i segni di interpunzione considerandoli elementi a sé stanti .

---

### Concetti Chiave del Capitolo

- **Cross-media Processing e Rappresentazione Visiva:** L'estensione delle capacità di recupero dell'informazione traducendo il testo descrittivo in interpretazioni astratte per la comparazione diretta con formati video o di immagine.

- **Interdipendenza tra NLP, IR e ML:** Le tre discipline tecnologiche non operano in compartimenti stagni; il Machine Learning modella gli output dell'Information Retrieval dopo che il testo è stato manipolato dai metodi del Natural Language Processing.

- **Processing Pipeline:** Un'architettura software a cascata in cui dati testuali grezzi attraversano vari moduli di pulizia, conversione e modifica, al fine di renderli digeribili e utili per gli algoritmi decisionali.

- **Estrazione e Pulizia (urllib & BeautifulSoup):** Strumenti Python impiegati rispettivamente per interrogare i server a basso livello al fine di scaricare il codice sorgente (urllib) e per setacciarlo ripulendolo dai tag del linguaggio di markup HTML/XML (BeautifulSoup).

- **NLTK e Tokenizzazione:** L'uso del Toolkit open source principe nel NLP per frammentare metodicamente stringhe continue di testo in unità di base (token), separando accuratamente parole ed elementi di punteggiatura per successive valutazioni statistiche o semantiche.

---

### Segmentazione delle Sentenze e Strumenti di Tokenizzazione Avanzati

Dopo aver estratto e pulito il testo, l'operazione successiva non si limita alla scomposizione in singole parole, ma richiede spesso la divisione del documento in frasi di senso compiuto. La **suddivisione di un testo in sotto-sentenze** (sentence splitting) non è affatto un compito banale. Sebbene l'uso della punteggiatura rappresenti l'indizio più rilevante e giochi un ruolo di primo piano nella costruzione e separazione delle frasi , il suo impiego per segnalare altre informazioni—come acronimi, numeri o iniziali—può indurre facilmente i sistemi in errore producendo divisioni errate. A titolo esemplificativo, si consideri la frase inglese: "I'll take the 8.30 train to St. Louis... or maybe I'll stay in LA. Time will tell!". Di fronte a questo testo, un algoritmo basilare faticherebbe a distinguere quali dei numerosi punti separino effettivamente le frasi e quali siano semplici abbreviazioni. Per risolvere questo problema, NLTK fornisce la funzione `sent_tokenize`, che agisce come un separatore di frasi consapevole della lingua, capace di gestire queste ambiguità.

Parallelamente, la scomposizione in singole parole può richiedere approcci dedicati. Il pacchetto `tokenize` di NLTK implementa segmentatori ideati per un gran numero di casi d'uso specifici. Tra questi troviamo strumenti per processare il testo di Twitter, il quale risulta molto informale e caratterizzato da uno stile peculiare della piattaforma. Sono inoltre disponibili tokenizzatori in grado di gestire espressioni composte da più parole (multi-word expressions) fornendo una lista di MWE in input , analizzatori basati su espressioni regolari , strumenti che si appoggiano al noto tool Stanford CoreNLP e ulteriori tokenizzatori specifici per particolari tipologie di dataset.

Una volta ottenuto un testo correttamente tokenizzato, si apre la strada ad alcune esplorazioni basilari. Diventa infatti possibile estrarre preziose informazioni strutturali: si può costruire un vocabolario dei termini utilizzati , contare la frequenza d'uso di ogni singolo termine , e persino tracciare graficamente la distribuzione di queste frequenze attraverso tutto il vocabolario. Inoltre, è possibile individuare le posizioni in cui i termini appaiono nel testo e visualizzare in quali contesti specifici vengano utilizzati. Per compiere rapidamente queste operazioni, gli oggetti `Text` e `FreqDist` offerti da NLTK si rivelano strumenti semplici ma estremamente efficaci.

### Normalizzazione del Testo: Stemming e Lemmatizzazione

Il linguaggio naturale presenta le parole in molteplici forme flesse. Lo **Stemming** e la **Lemmatizzazione** (talvolta riferita informalmente come *Lemming*) sono due tecniche complementari che mirano a ridurre le diverse declinazioni e coniugazioni di una parola alla sua radice fondamentale .

[RIFERIMENTO VISIVO DEL PROFESSORE: Diagramma che mostra le parole derivate "Dancing", "Danced", "Dancer" e "Dances" che convergono tutte verso la singola radice comune "Dance" ].

Nello specifico, lo **Stemming** esegue questo processo applicando un insieme di regole di trasformazione della parola che sono strettamente dipendenti dalla lingua in uso. Poiché si tratta di un approccio prettamente algoritmico basato su troncamenti, il risultato può produrre una radice che è lessicalmente scorretta o priva di significato autonomo. Utilizzando il modulo `PorterStemmer` di NLTK, possiamo osservare come la parola al plurale "cars" venga correttamente ridotta al singolare "car" . Tuttavia, applicando lo stesso algoritmo al verbo "was", l'output risultante è il moncone "wa", una parola grammaticalmente inesistente .

Al contrario, la **Lemmatizzazione** adotta un'analisi NLP molto più profonda e, di conseguenza, computazionalmente più costosa. Il suo obiettivo non è troncare, ma ricondurre una parola esattamente alla sua forma da dizionario, il cosiddetto "lemma".

[INSERIRE IMMAGINE: Estratto di un dizionario che mostra le definizioni di parole come "dictatorial", "diction" e "dictionary", utile a illustrare il concetto di ricerca della forma base ].

Implementando l'oggetto `WordNetLemmatizer` di NLTK, notiamo differenze cruciali rispetto allo stemming . La parola "cars" viene nuovamente ridotta a "car" . Quando si valuta la parola "was", l'algoritmo di base restituisce ancora "wa", poiché la lemmatizzazione richiede di conoscere preventivamente la *Part of Speech* (POS), ovvero la categoria grammaticale della parola analizzata. Se infatti forniamo al lemmatizzatore l'informazione aggiuntiva che "was" svolge la funzione di verbo (impostando il parametro `pos='v'`), l'analisi si affina e restituisce correttamente il lemma base "be" (essere) .

### Verso una Rappresentazione Computazionale: Il Modello Bag of Words

Finora, le *feature* (le caratteristiche) sono state estratte dal testo sotto forma di liste che riflettono l'esatta sequenza delle parole, comprensive di eventuali ripetizioni. Prendendo in esame la frase "the president of the united states of america" ed eseguendo la consueta `word_tokenize`, otteniamo una lista di 8 elementi contenente i singoli token in ordine .

Tuttavia, articoli (come "the") e preposizioni (come "of") tendono a ripetersi molto spesso nei documenti testuali. Rappresentare il testo conservando queste liste a lunghezza variabile, piene di elementi ridondanti, costituisce un notevole problema tecnico per la maggior parte degli algoritmi di Machine Learning, i quali necessitano tipicamente di strutture dati standardizzate e a lunghezza fissa.

Per superare questo ostacolo, il modello più frequentemente adottato è il **Bag of Words (BOW)**. Questo paradigma concettuale rappresenta un documento considerando unicamente l'insieme (in senso matematico) delle parole da cui è composto, ignorando le ripetizioni multiple. A livello di codice, questo si traduce nel convertire la lista dei token in un *set* di Python (`bow = set(feats)`). Eseguendo questa conversione sulla frase d'esempio, la lunghezza della struttura dati passa da 8 a 6, producendo il seguente insieme non ordinato di token unici: `['america', 'of', 'president', 'states', 'the', 'united']` . In questo modo, il modello riduce drasticamente la lunghezza dei vettori dei token estrapolando esclusivamente il vocabolario base che definisce il testo in esame.

---

### Concetti Chiave del Capitolo

- **Segmentazione delle Sentenze:** Il delicato processo di divisione di un documento in frasi di senso compiuto, reso complesso dall'ambiguità sintattica della punteggiatura utilizzata per acronimi e numeri.

- **Stemming:** Una tecnica veloce di normalizzazione che usa regole predefinite per troncare le parole e ricondurle alla loro radice morfologica, col rischio di generare termini lessicalmente invalidi (es. "was" che diventa "wa").

- **Lemmatizzazione:** Un'analisi linguistica profonda che sfrutta l'identificazione grammaticale (Part of Speech) per convertire correttamente una parola flessa nel suo lemma di dizionario (es. "was" che diventa "be").

- **Bag of Words (BOW):** Un modello di rappresentazione testuale che descrive un documento come un puro insieme di parole uniche (set), ignorando sia l'ordine originale dei vocaboli sia le loro ripetizioni, semplificando così i dati per il Machine Learning.

---

### I Limiti del Modello Bag of Words e l'Introduzione degli N-grammi

Il modello Bag of Words (BOW), per sua stessa definizione, riduce drasticamente la complessità computazionale e la lunghezza dei vettori dei token, ma lo fa a un prezzo elevato: esso perde inesorabilmente le informazioni relative all'ordine in cui le parole compaiono e alla loro frequenza di utilizzo. Sebbene le conteggi delle occorrenze di ciascuna parola possano essere salvate e gestite in strutture dati addizionali per non perdere l'informazione sulla frequenza, il problema dell'ordine strutturale rimane. L'insieme risultante di tutte le feature distinte estratte (che in ambito tecnico può essere definito in modo intercambiabile come vocabolario, dizionario, *feature set* o *feature space* ) non è in grado di cogliere il senso logico della frase originaria.

Un esempio lampante di questa criticità si evince confrontando due frasi dal significato diametralmente opposto: "I won, and thus you lose." (Ho vinto, e quindi tu perdi) e "I lose, and thus you won." (Ho perso, e quindi tu hai vinto) . Applicando il modello BOW, le due stringhe generano lo stesso identico set di parole non ordinate: `['and', 'lose', 'thus', 'won', 'you']` . Di conseguenza, per il calcolatore i due vettori risultano perfettamente identici (`t1 == t2` restituisce `True`), rendendo impossibile per un algoritmo distinguere frasi con significati diversi ma vocabolario sovrapponibile .

Per ovviare a questa grave perdita di significato posizionale, si introduce il concetto di **Word N-grams** (N-grammi di parole). Un N-gramma è semplicemente una sequenza di *n* elementi consecutivi estratti da un testo, che permette di rappresentare il linguaggio in termini di "pezzi locali", aggiungendo ordine e contesto al vettore delle feature. Riprendendo le frasi precedenti e applicando la funzione `nltk.ngrams` con una dimensione pari a 2 (creando quindi dei 2-grammi o bigrammi), l'algoritmo non isola più le singole parole, ma le coppie consecutive. La prima frase produrrà feature composite come `W2G_won_and` e `W2G_you_lose`, mentre la seconda genererà `W2G_lose_and` e `W2G_you_won` . Pertanto, il confronto logico tra i due set restituirà correttamente `False`, riflettendo la diversità concettuale delle due espressioni . Il formato preciso con cui vengono denominate queste nuove feature (come l'aggiunta del prefisso `W2G_`) è irrilevante ai fini pratici, purché non si crei ambiguità tra le feature estratte da metodi differenti.

Questa logica di raggruppamento sequenziale può essere applicata anche a livello sub-lessicale, dando origine ai **Character N-grams** (N-grammi di caratteri). Eseguendo l'algoritmo sulle singole parole anziché sull'intera frase, si scompone il vocabolo in sequenze di lettere. Questa tecnica si rivela estremamente utile per mitigare l'effetto dei refusi e degli errori di battitura (typos). Se si confronta la corretta ortografia "rainbow" con la forma errata "rainbaw" utilizzando dei 3-grammi di caratteri, si otterranno due set di feature in gran parte sovrapponibili . L'intersezione tra i due insiemi dimostrerà infatti che i due termini condividono svariati N-grammi (come `C3G_a_i_n`, `C3G_i_n_b`, e `C3G_r_a_i`), segnalando all'algoritmo una fortissima similarità strutturale nonostante l'errore di digitazione .

### La Legge di Zipf e la Frequenza delle Parole

Analizzando il linguaggio da una prospettiva puramente statistica, emerge un fenomeno universale: la distribuzione delle parole in un qualsiasi testo linguistico non è uniforme, ma segue rigorosamente la **Legge di Zipf**. Questa legge empirica stabilisce che la frequenza di utilizzo di una parola in un testo è inversamente proporzionale al suo rango (la sua posizione) nella classifica globale delle frequenze del documento. In termini matematici, indicando con $r$ il rango (dove 1 spetta alla parola più frequente, 2 alla seconda e così via) e con $f(r)$ la frequenza, si ha la relazione $f(r) \propto 1/r$ . Per dare un'idea dell'impatto di questo squilibrio, basti pensare che in un noto dataset di test, il Reuters 21578, appena 313 vocaboli distinti riescono a coprire da soli la metà dell'intero volume di 500.000 occorrenze totali.

Questa distribuzione altamente sbilanciata viene teoricamente giustificata dal **Principio del minor sforzo** (Principle of least effort), una dinamica psicologica e comunicativa insita nell'evoluzione umana che punta a minimizzare lo sforzo complessivo durante una conversazione . In ogni scambio esistono due attori in competizione: il parlante tende a voler usare un vocabolario ristretto, prediligendo parole molto comuni, magari corte e facili da riprodurre, per sforzarsi il meno possibile nel comunicare. L'ascoltatore, al polo opposto, beneficerebbe di un vocabolario enorme, popolato da termini altamente specifici, rari e inequivocabili, in modo da decodificare il messaggio con il minimo sforzo interpretativo e senza ambiguità . La curva tracciata dalla Legge di Zipf rappresenta l'esatto punto di compromesso evolutivo tra queste due istanze contrastanti.

Da questo postulato derivano due leggi correlate altrettanto interessanti: vi è una relazione inversa tra la frequenza d'uso di un termine e la sua lunghezza grafica, e si osserva che anche il numero di significati diversi $m$ associati a una parola obbedisce a una legge inversa rispetto alla sua frequenza ($m \propto 1/f$) .

[INSERIRE IMMAGINE: Grafico a dispersione che illustra la curva decadente della Legge di Zipf, ponendo in relazione il numero di feature sull'asse delle ordinate (in scala logaritmica) e la Document Frequency (DF) sull'asse delle ascisse] .

### L'Ottimizzazione del Vocabolario: Stopwords e Parole Rare

Le conseguenze della Legge di Zipf incidono direttamente su come i sistemi informatici filtrano i testi. Agli estremi della curva troviamo, da un lato, parole onnipresenti, e dall'altro parole uniche. Si osserva che le parole più comuni di una determinata lingua—come gli articoli ("il", "lo", "the", "a"), le preposizioni ("di", "con", "of") o le congiunzioni—non contribuiscono in modo significativo alla reale comprensione semantica del documento in cui si trovano. Questi vocaboli vengono denominati **Stopwords**.

[INSERIRE IMMAGINE: Il medesimo grafico a dispersione della Legge di Zipf, nel quale viene però evidenziata in rosso la sezione in basso a destra, corrispondente ai vocaboli con Document Frequency estrema, etichettandoli come "stopwords"] .

Rimuovere queste Stopwords dal testo abbatte notevolmente il carico computazionale senza comportare una sensibile perdita di informazione. Ad esempio, trasformando la frase originale in `president united states america`, l'intento comunicativo rimane chiarissimo. I pacchetti NLP moderni, come la libreria NLTK, forniscono elenchi predefiniti per numerose lingue; importando `stopwords.words('english')` ed eseguendo una sottrazione insiemistica (`difference`) tra le feature estratte e la lista di Stopwords, i vocaboli accessori vengono automaticamente filtrati via . Tuttavia, l'uso di liste preconfezionate non è sempre raccomandabile per tutti gli scenari applicativi: la lista predefinita del database MySQL, per esempio, include tra le sue stopwords parole come "appreciate" (apprezzare), "serious" (serio) e "unfortunately" (sfortunatamente), vocaboli carichi di significato che si rivelano assolutamente cruciali se lo scopo dell'analisi è eseguire una classificazione del sentimento testuale (Sentiment Analysis) .

All'estremità opposta della curva troviamo le **Parole rare** (Rare features), vocaboli di nicchia che possiedono una frequenza bassissima.

[INSERIRE IMMAGINE: Il consueto grafico a dispersione, dove questa volta viene evidenziata in rosso la sezione in alto a sinistra, che racchiude la grande quantità di termini che possiedono una Document Frequency quasi nulla, indicati come "rare words"] .

I termini che compaiono in pochissimi documenti non apportano un'informazione statistica utile a generalizzare il modello di apprendimento automatico che stiamo addestrando. Se una parola appare raramente nei testi osservati nel passato, con altissima probabilità continuerà a presentarsi raramente anche nei testi futuri, rendendola di scarsa utilità per il processamento di nuovi input . Tali parole compaiono spesso una singola volta e costituiscono una fetta sorprendentemente ampia delle parole distinte in una collezione; esse sono perlopiù frutto di errori di battitura casuali o derivano da identificatori artificiali legati unicamente alla formattazione del documento specifico, come può essere lo slug di un indirizzo web . La potatura sistematica di queste parole rare dal vocabolario rende non solo nettamente più veloce il processamento del testo indicizzato, ma riduce anche drasticamente lo spazio di memoria richiesto dal calcolatore.

### Il Vector Space Model (VSM)

Avendo pulito il testo e normalizzato il vocabolario, l'ultimo gradino della pipeline di strutturazione consiste nel quantificare l'informazione trasformando formalmente l'insieme di parole di ogni documento in un vettore algebrico; questo approccio è il cuore del **Vector Space Model (VSM)**.

[INSERIRE IMMAGINE: Diagramma geometrico bidimensionale che raffigura il Vector Space Model; un piano cartesiano definito dall'asse 'feature 1' e dall'asse 'feature 2', all'interno del quale i documenti testuali (d1="best days of the year", d2="nice weather on the weekend", d3="risk of thunderstorms") sono tracciati come vettori (frecce) orientati a partire dall'origine] .

In questo modello, ogni documento viene mappato come un vettore $v$ definito all'interno di uno spazio a $|F|$ dimensioni, dove $|F|$ rappresenta il numero esatto (la cardinalità) di tutte le feature o token distinti estratti dal corpus analizzato. L'ipotesi geometrica alla base del VSM è affascinante e intuitiva: se i vettori di due documenti nello spazio cartesiano sono orientati l'uno vicino all'altro (ossia presentano un angolo molto ristretto), è estremamente probabile che quei due documenti condividano argomenti simili e siano semanticamente affini.

La transizione dallo spazio testuale a quello vettoriale richiede l'assegnazione di ogni singola feature a una dimensione univoca dello spazio $\mathbb{R}^{|F|}$. Questo si ottiene generando una matrice identità in cui si crea per ciascun token un **one-hot vector**, ossia un vettore che possiede tutti i valori impostati a 0, ad eccezione della singola posizione corrispondente a quella specifica parola, che viene marcata con un 1. In questa struttura basilare, il vocabolo 'I' potrebbe assumere la forma $v('I') = [1, 0, \dots, 0, 0]$, il termine 'you' sarà $v('you') = [0, 1, \dots, 0, 0]$ e così via, per ogni token esistente nel dizionario .

[RIFERIMENTO VISIVO DEL PROFESSORE: Esempio discusso alla lavagna raffigurante la costruzione dei vettori binari partendo da due query in input (Q1: "I love Information Retrieval course" e Q2: "Giovanni hates eat apples"). Viene mostrata la corrispondenza univoca tra ogni termine e un indice numerico (es. 0 per 'Love', 1 per 'Information'...), originando i due vettori finali Q1 <1,1,1,1,0,0,0,0> e Q2 <0,0,0,0,1,1,1,1>] .

Una volta definite le dimensioni per i singoli vocaboli, l'intero documento viene espresso analiticamente come la somma pesata dei vettori delle feature che vi sono contenute. La formula formale è:

$$v(d) = \sum_{f \in d} w_{fd} v(f)$$

In questa equazione, il termine $w_{fd}$ rappresenta la rilevanza (il peso) della specifica feature $f$ all'interno del documento $d$ . Pertanto, un breve documento composto dalla frase $d =$ 'you played a good game' si tramuterà nel vettore $v(d) = [0, w_{played,d}, w_{game,d}, 0, \dots, w_{good,d}, 0, \dots, 0, 0]$ .

Da questa composizione emerge immediatamente un aspetto strutturale vitale: la stragrande maggioranza dei valori contenuti in questi vettori è pari a zero, determinando quella che in algebra lineare viene definita **sparsità**. Essendo la lunghezza del vettore pari alla totalità dei termini dell'intero vocabolario e contenendo un singolo documento solo una frazione irrisoria di tutte quelle parole, si ottiene un **vettore sparso**, la cui formula matematica enuncia che la cardinalità degli elementi non nulli del vettore è grandemente inferiore all'intero numero di feature esistenti ($|\{i | v_i(d) \neq 0\}| \ll n$) .

L'interrogativo cruciale che chiude questa fase di modellizzazione, e che fa da ponte verso gli algoritmi di pesatura avanzati, consiste nello stabilire con quale esatto criterio e valore si debbano impostare questi pesi $w_{fd}$ in modo che riflettano la reale importanza del termine all'interno del corpus testuale.

---

# Slide 3: Punteggi, Pesi dei Termini e Modello Spaziale Vettoriale

Questo capitolo introduce il passaggio dai sistemi di recupero logici classici ai modelli di **Ranked Retrieval**, esplorando le tecniche fondamentali di calcolo dei punteggi per ordinare i documenti, come la frequenza dei termini (TF) e la frequenza inversa dei documenti (IDF).

### L'Architettura di un Motore di Ricerca

Il processo operativo di un sistema di Information Retrieval è suddiviso in due macro-fasi. La fase offline gestisce la **Document Collection** tramite il processo di **Indexing**, il quale ha lo scopo di generare un **Inverted Index**. Parallelamente, un **Feature Processor** estrae i dati per popolare il **Document Features Repository** e per fornire i dati di training (Training Data) necessari all'addestramento del modello **Learning-to-rank**. La fase online, invece, riceve la **Query** iniziale dell'utente ed elabora una **Expanded Query**. Successivamente, attraverso il **Query Processing** e una fase di calcolo delle caratteristiche (Feature Lookup and Computation), il sistema sfrutta la funzione di ranking precedentemente appresa per generare e mostrare i risultati.
[INSERIRE IMMAGINE: Diagramma a blocchi dell'architettura di un sistema di Information Retrieval, che separa chiaramente i processi offline di indicizzazione da quelli online di processamento della query.]

### Dal Boolean Search al Ranked Retrieval

Nei sistemi più semplici, le query sono rigorosamente booleane: i documenti corrispondono ai criteri richiesti oppure vengono del tutto scartati. Questo paradigma si rivela eccellente per gli utenti esperti, che hanno una profonda comprensione dei propri bisogni informativi e della collezione, o per applicazioni in grado di consumare agilmente migliaia di risultati. Al contrario, questo approccio non è ottimale per l'utente comune, il quale si dimostra spesso incapace o riluttante a comporre complesse espressioni logiche e non desidera affatto vagliare manualmente migliaia di risultati. Nel contesto della web search, il limite del modello booleano è comunemente noto come la sindrome del "**feast or famine**" (abbondanza o carestia): le ricerche tendono a produrre o zero risultati oppure un numero incontrollabile di hit, poiché l'uso dell'operatore AND risulta troppo restrittivo e l'uso dell'operatore OR troppo permissivo.

Per risolvere queste criticità, si è passati al **Ranked Retrieval**. In questo modello, il sistema riordina i documenti della collezione proponendo all'utente i migliori per una data **Free-Text query**. La ricerca si basa quindi su una o più parole formulate in linguaggio naturale, senza l'uso di complessi operatori. Con il ranked retrieval, il problema delle enormi liste di risultati scompare, poiché il sistema presenta ordinatamente solo i top $k$ documenti (tipicamente $\approx 10$), non sovraccaricando mai l'operatore umano. La premessa di base è, ovviamente, che l'algoritmo di ranking alla base funzioni a dovere.

### Il Concetto di Scoring e il Coefficiente di Jaccard

Il fulcro del ranked retrieval è il calcolo dello **Score**. Lo scopo è assegnare a ciascun documento un punteggio, ad esempio compreso nell'intervallo $[0,1]$, che misuri l'affinità con la query dell'utente, in modo da poter restituire per primi i testi ritenuti più utili.

Un primo rudimentale indicatore matematico è il **Coefficiente di Jaccard**, impiegato per quantificare la sovrapposizione tra due insiemi A (la query) e B (il documento). La sua formula è $jaccard(A,B)=|A\cap B|/|A\cup B|$. La metrica restituisce sempre un valore tra 0 e 1, assegnando 1 quando gli insiemi coincidono perfettamente e 0 quando l'intersezione è nulla, senza richiedere che A e B abbiano la stessa cardinalità. Se prendiamo come esempio la query "ides of march" e due documenti $d_1$ ("caesar died in march") e $d_2$ ("the long march"), il punteggio calcolato è rispettivamente $J(q,d1)=1/(3+4-1)=1/6$ e $J(q,d2)=1/(3+3-1)=1/5$, suggerendo che $d_2$ sia più simile alla richiesta rispetto a $d_1$.

Tuttavia, il Coefficiente di Jaccard si dimostra presto insufficiente per l'Information Retrieval avanzato. Poiché modella i testi come semplici "insiemi" (in linea con la logica booleana), fallisce nel considerare la **Term Frequency**, ignorando completamente il numero di volte in cui una parola occorre nel testo e l'elevato valore informativo che i termini rari detengono rispetto a quelli più inflazionati.

### Rappresentazione dei Documenti e Term Frequency (TF)

Nel tentativo di incorporare il peso delle occorrenze, si passa dalla semplice matrice binaria di incidenza (dove ogni documento è descritto da vettori in $\{0,1\}^{|V|}$) a matrici di conteggio termine-documento. In questo nuovo modello spaziale, noto come **Bag of Words** (o multinsieme), ciascun documento diventa un vettore di conteggio nello spazio $\mathbb{N}^{v}$. Questo approccio si focalizza puramente sulle quantità, ignorando totalmente l'ordinamento sequenziale delle parole nel testo (rendendo indistinguibili frasi invertite logicamente ma composte dagli stessi lemmi).

In questo paradigma prende forma la **Term Frequency** ($tf_{t,d}$), intesa strettamente come il mero conteggio delle apparizioni di un termine $t$ all'interno di un documento $d$. Applicarla in modo grezzo, tuttavia, creerebbe distorsioni: se è vero che un testo con 10 occorrenze di una parola chiave debba avere uno score più alto di uno con 1 singola occorrenza, è ugualmente vero che esso non sarà esattamente "10 volte" più rilevante. L'aumento della rilevanza, infatti, non è direttamente proporzionale alla crescita della frequenza matematica.

Per ammorbidire questo sbilanciamento si introduce il **Log-frequency weighting**, una funzione logaritmica concepita per smorzare l'effetto dei conteggi estremi. Il nuovo peso del termine diviene $w_{t,d} = 1 + log_{10}tf_{t,d}$ nei casi in cui $tf_{t,d} > 0$, restando fermo a 0 in caso contrario. Attraverso questa correzione, progressioni aritmetiche marcate come 1, 10, 1000 vengono compresse linearmente verso i punteggi 1, 2 e 4. Lo score di match query/documento diventa quindi la sommatoria dei pesi smorzati per i termini in comune: $score_{qd}=\sum_{t\in q\cap d}(1+log~tf_{t,d})$.
[INSERIRE IMMAGINE: Grafico a curva logaritmica che descrive la progressione smorzata del peso in relazione all'aumento lineare della term frequency.]

### Document Frequency e Inverse Document Frequency (IDF)

In aggiunta alle occorrenze testuali, un buon sistema di ranking deve saper isolare l'efficacia intrinseca delle parole all'interno della macro-collezione. È un principio fondante che i termini globalmente rari (es. "arachnocentric") siano indicatori d'informazione nettamente superiori rispetto alle parole ad altissima frequenza d'uso. Una pagina web che contiene parole inusuali presenti in una query ha una forte probabilità di essere inerente alla ricerca effettuata. Inverso è il caso di parole onnipresenti ("high", "increase", "line"): pur garantendo un livello base di affinità, non offrono certezze qualitative poiché figurano in una fetta eccessivamente larga dell'archivio.

Il fattore impiegato per formalizzare questa intuizione è la **Document Frequency** ($df_{t}$), definita come il quantitativo totale di documenti, all'interno di una collezione grande $N$, che ospitano il vocabolo $t$. Avere un elevato valore di $df_{t}$ implica una bassa informatività concettuale. Ribaltando matematicamente questo indicatore nasce l'**Inverse Document Frequency (IDF)**. Per addolcire l'impatto di collezioni testuali sterminate, la formula definitiva applica anch'essa un logaritmo al rapporto di questi valori: $idf_{t}=log_{10}(N/df_{t})$.

Questo punteggio di rarità è unico e immutabile per ciascun termine nella collezione prescindendo dalla query attiva, rendendolo precalcolabile offline. A titolo esemplificativo, analizzando un volume ipotetico di un milione di documenti, articoli molto comuni avranno un divisore identico al totale, producendo un peso nullo ($IDF=0$), mentre termini rintracciabili in un solo documento arriveranno a ottenere un poderoso moltiplicatore pari a 6.
[INSERIRE IMMAGINE: Discesa asintotica della curva Inverse Document Frequency lungo l'asse della Document Frequency in costante crescita.]

### Glossario / Concetti Chiave

- **Ranked Retrieval**: Metodologia di Information Retrieval che supera il limite del modello booleano proponendo risultati in liste ordinate decrescenti in base all'utilità, tipicamente accettando in input espressioni di testo libero.

- **Coefficiente di Jaccard**: Misura matematica basata unicamente sull'intersezione rispetto all'unione delle keyword, utile come approccio teorico di similarità ma inadatta all'IR avanzato perché ignora il peso quantitativo e qualitativo delle parole.

- **Term Frequency (TF)**: Calcolo formale delle presenze di un singolo elemento lessicale interno al testo analizzato; le sue manifestazioni numeriche dirette vengono logaritmicamente smorzate per evitare che il peso cresca linearmente.

- **Inverse Document Frequency (IDF)**: Penalità proporzionale imposta ai vocaboli dominanti in una collezione; favorisce nel ranking i lemmi scarsamente impiegati aumentandone vertiginosamente la rilevanza ai fini della query.

---

## Pesi TF-IDF e il Modello Spaziale Vettoriale

Questo capitolo approfondisce l'impiego della frequenza inversa dei documenti nel calcolo del ranking, per poi introdurre lo schema di pesatura più celebre dell'Information Retrieval e la rappresentazione geometrica dei documenti e delle query.

### L'Effetto dell'IDF sul Ranking e la Frequenza nella Collezione

A questo punto ci si potrebbe chiedere se l'**Inverse Document Frequency (IDF)** abbia un effetto tangibile sul ranking nel caso di query composte da un singolo termine, come ad esempio la ricerca isolata della parola "iPhone". La risposta è negativa, poiché l'IDF da solo non è in grado di ordinare i documenti per query basate su un solo termine. In questo specifico scenario, tutti i documenti all'interno dei quali si verifica l'occorrenza del termine ottengono esattamente lo stesso punteggio, calcolato sommando i logaritmi in base 10 del rapporto $N/df_{t}$ per tutti i termini presenti sia nella query che nel documento: $Score_{qd}=\sum_{t\in qnd}log_{10}(\frac{N}{df_{t}})$. Di conseguenza, l'impiego esclusivo dell'IDF può produrre un vero e proprio ordinamento (ranking) dei documenti corrispondenti soltanto per le query contenenti almeno due termini, sempre a patto che i documenti non contengano tutti quanti i vocaboli ricercati. Questo principio si comprende facilmente immaginando una query come "capricious person". In questa circostanza, la pesatura IDF farà in modo che le occorrenze del termine "capricious" – che risulta essere molto più raro a livello globale – contino molto di più nel calcolo del punteggio finale del documento rispetto alle occorrenze della diffusissima parola "person".

Per valutare la rarità e l'efficacia di una parola chiave, è inoltre essenziale non confondere la frequenza nei documenti con la **Collection Frequency** ($cf_t$). Quest'ultima misura il numero totale e assoluto di occorrenze di un termine $t$ all'interno dell'intera collezione, conteggiando quindi anche le occorrenze multiple che si verificano ripetutamente all'interno di ogni singolo documento, un principio strettamente legato all'andamento statistico descritto dalla legge di Zipf. Per osservare l'evidente differenza pratica tra i due concetti, possiamo analizzare la seguente tabella comparativa:

| **Word**  | **Collection frequency** | **Document frequency** |
| --------- | ------------------------ | ---------------------- |
| Insurance | 10440                    | 3997                   |
| Try       | 10422                    | 8760                   |
|           |                          |                        |

Di fronte a questi due indicatori, sorge spontaneo domandarsi quale delle due parole rappresenti un termine di ricerca migliore, meritevole di un peso maggiore nel motore di ricerca. Osservando i dati, risulta palese che il lemma "Insurance", pur avendo una presenza totale nella collezione molto simile al verbo "Try", è distribuito in meno della metà dei testi (una Document frequency nettamente inferiore), risultando perciò molto più distintivo, informativo e utile per filtrare i risultati della ricerca.

### Lo Schema di Pesatura TF-IDF

[INSERIRE IMMAGINE: Medaglia d'oro con il numero 1, a simboleggiare che il TF-IDF è il miglior e più noto schema di pesatura nel campo dell'Information Retrieval]

Avendo chiarito le dinamiche dell'IDF, si pone il problema matematico di come pesare in maniera corretta i termini dei documenti all'interno dei vettori. L'intuizione concettuale che guida questa misurazione si basa su due pilastri fondamentali. In primo luogo, i termini che compaiono con alta frequenza in un documento – parametro misurato dalla **Term Frequency** (TF) – dovrebbero ricevere pesi elevati. Il motivo è puramente logico: quanto più spesso un documento contiene, ad esempio, la parola "dog", tanto maggiore è la probabilità che il documento tratti effettivamente di cani. In secondo luogo, però, i vocaboli che appaiono in moltissimi documenti differenti – parametro tracciato dalla **Document Frequency** (DF) – dovrebbero ottenere pesi decisamente più bassi. È il caso di congiunzioni, articoli o preposizioni come "the", "a" oppure "of", che figurano nella quasi totalità dei documenti archiviati e risultano sprovvisti di un peso semantico discriminante.

Per tradurre questa intuizione in una formula matematica solida, l'Information Retrieval coniuga la Term Frequency con la Inverse Document Frequency. Nasce così lo schema di pesatura **TF-IDF**, nel quale il peso di un termine è strutturato come il prodotto esatto del suo peso TF (espresso come frequenza normalizzata $wf$) moltiplicato per il suo peso IDF. Delineando le formule, se $idf_{t}=log\frac{N}{df_{t}}$ e il peso normalizzato $wf_{t,d}$ è calcolato come $1+log~tf_{t,d}$ (restituendo 1 + logaritmo per frequenze maggiori di zero, e zero assoluto altrimenti), il calcolo definitivo diverrà $wf-idf_{t,d}=wf_{t,d}\times idf_{t}$. Esiste anche un'alternativa in cui si riduce l'impatto del termine TF adottando una formula logaritmica più contenuta, pari a $log(1+tf_{t,d})$. Ad oggi, questo rappresenta il più conosciuto e apprezzato schema di pesatura nel settore. Occorre precisare, per evitare fraintendimenti sintattici, che il trattino nel nome "TF-IDF" è un semplice segno grafico di interpunzione e non deve essere interpretato come un segno di sottrazione; a conferma di ciò, la metrica viene spesso menzionata con le notazioni alternative tf.idf oppure tf x idf. Riassumendo le sue proprietà, il peso restituito dalla formula incrementa di pari passo con il numero di occorrenze della parola all'interno del documento analizzato, e si innalza simultaneamente grazie alla generale rarità del termine nell'intera collezione dei dati.

Nel momento in cui si richiede di produrre un punteggio totale – o Score – per accoppiare un documento $d$ a una query $q$, il risultato sarà la semplice somma progressiva dei pesi TF-IDF per ciascun termine isolato che compare contemporaneamente nell'intersezione tra la query e il documento in esame. I motori di ricerca permettono l'adozione di moltissime varianti operative di questa base teorica. Le differenze si riscontrano in base a come il punteggio TF viene matematicamente calcolato, decidendo per esempio se applicare i logaritmi o se discretizzare i valori delle frequenze, e scegliendo, inoltre, se estendere la medesima pesatura logica anche ai termini originariamente digitati nella query.

### Calcolo Pratico dei Pesi TF-IDF

Per trasformare questa astrazione in un calcolo verificabile, supponiamo di dover analizzare una determinata collezione testuale composta da un numero totale di documenti $N$ equivalente a 806.791. Il nostro obiettivo è calcolare con precisione i pesi TF-IDF per i termini della query "car", "auto", "insurance" e "best" all'interno di tre specifici documenti, sfruttando l'IDF generato sulla collezione. I parametri precalcolati e l'applicazione dell'IDF (secondo l'espressione $idf_{t}=log\frac{N}{df_{t}}$) sono racchiusi in questa tabella strutturale:

| **Termine** | **Doc1 (tft​)** | **Doc2 (tft​)** | **Doc3 (tft​)** | **dft​** | **idft​** |
| ----------- | --------------- | --------------- | --------------- | -------- | --------- |
| car         | 27              | 4               | 24              | 18,165   | 1.65      |
| auto        | 3               | 33              | 0               | 6723     | 2.08      |
| insurance   | 0               | 33              | 29              | 19,241   | 1.62      |
| best        | 14              | 0               | 17              | 25,235   | 1.5       |
|             |                 |                 |                 |          |           |

Procediamo al calcolo effettivo per il Doc1 applicando la normale formula per moltiplicazione $tf_{t,d}\times idf_{t}$. Il termine "car", occorrendo 27 volte nel testo originale, genera un peso di $27\times1.65$, per un totale di 44.55. Il lemma "insurance", del tutto assente dal documento, incide con un risultato pari a 0. Valutando il vocabolo "auto", il punteggio riscontrato si attesta a $3\times2.08 = 6.24$, mentre l'ultimo indicatore "best" conferisce uno score massiccio di $14\times1.5 = 21$. Nel caso in cui gli ingegneri del sistema di ricerca optassero per utilizzare la funzione logaritmica moderata espressa da $log(1+tf_{t,d})$, il fattore $idf_{t}$ finirebbe per ricoprire un'importanza quantitativamente molto più profonda all'interno dell'economia dei risultati. Eseguendo di nuovo i calcoli per il medesimo Doc1, scopriamo come le frequenze vengano attenuate: la parola "car" produrrà in questo caso uno score di appena $1.45\times1.65 = 2.39$. Coerentemente, il lemma "insurance" perdura a 0, ma "auto" fa segnare un modesto risultato di $0.6\times2.08 = 1.2543$ e "best" frena i suoi risultati stabilizzandosi su $1.18\times1.5 = 1.764$.

### Il Modello Spaziale Vettoriale: Dai Conteggi ai Pesi

L'implementazione algoritmica di questi punteggi converte i metodi di misurazione dei dati testuali in veri e propri enti matematici, compiendo una transizione da un formato binario primordiale, per passare ai meri conteggi posizionali, fino ad assestarsi come una sofisticata **Weight Matrix** basata sui pesi relazionali. Se per visualizzare i concetti evochiamo l'esempio del materiale shakespeariano, l'impalcatura che ordina i testi assumerà una matrice a valori reali decodificabile come segue:

| **Termine** | **Antony and Cleopatra** | **Julius Caesar** | **The Tempest** | **Hamlet** | **Othello** | **Macbeth** |
| ----------- | ------------------------ | ----------------- | --------------- | ---------- | ----------- | ----------- |
| Antony      | 5.25                     | 3.18              | 0               | 0          | 0           | 0.35        |
| Brutus      | 1.21                     | 6.1               | 0               | 1          | 0           | 0           |
| Caesar      | 8.59                     | 2.54              | 0               | 1.51       | 0.25        | 0           |
| Calpurnia   | 0                        | 1.54              | 0               | 0          | 0           | 0           |
| Cleopatra   | 2.85                     | 0                 | 0               | 0          | 0           | 0           |
| mercy       | 1.51                     | 0                 | 1.9             | 0.12       | 5.25        | 0.88        |
| worser      | 1.37                     | 0                 | 0.11            | 4.15       | 0.25        | 1.95        |
|             |                          |                   |                 |            |             |             |

Questa configurazione denota un cambiamento sostanziale: ciascun singolo documento viene infatti modellato come un vettore composto da valori reali – i pesi TF-IDF – iscritto in uno spazio che si definisce su una base di tipo $\mathbb{R}^{|V|}$, al cui interno il simbolo V rappresenta l'intero e totale vocabolario riscontrato nella collezione esaminata. Viene così introdotto quello che nella teoria accademica prende il nome di **Modello Spaziale Vettoriale** (Vector Space Model). Stiamo manipolando uno spazio vettoriale la cui dimensione assoluta corrisponde a $|V|$; l'elemento fondante prevede che i singoli termini racchiusi nel vocabolario fungano in prima persona da assi di orientamento cartesiano dello spazio stesso, così che i testi in analisi diventino dei semplici punti di congiunzione, o più propriamente vettori, ancorati in questo paesaggio multidimensionale. Traslando tale concezione astratta all'interno dell'operatività di un tipico motore di web search, stiamo affrontando un contesto dalla dimensionalità sbalorditiva, quantificabile in svariate decine di milioni di dimensioni differenti. I vettori originati per mappare questa complessità esibiscono una proprietà marcata che li classifica come vettori altamente sparsi, ovvero insiemi dove la stragrande maggioranza dei valori numerici in entrata corrisponde a zero perfetto.

La transizione finale si attua mediante due intuizioni cruciali (o "Key ideas") progettate per processare similmente anche le richieste inoltrate dagli utenti, trattando fondamentalmente le **Queries as vectors**. La prima intuizione detta di replicare lo stesso protocollo costruttivo usato sui documenti sorgente: si codificano di conseguenza anche le query trasformandole in vettori posizionati all'interno di questo grande spazio concettuale. La seconda intuizione fondamentale si manifesta riordinando tutti i documenti unicamente in base alla loro geometrica "proximity" o vicinanza formale rispetto al nuovo vettore tracciato dalla query dello spettatore. La metrica della prossimità si traduce nella rilevazione formale della "similarity" intercorsa fra detti vettori spaziali. Secondo le logiche dimensionali, si desume chiaramente che questa prossimità rappresenti semplicemente il correlato inverso della distanza matematica verificabile tra due punti. Riepilogando le innovazioni maturate in questa parte del sistema teorico, lo schema rammenta costantemente l'obbiettivo principale per cui questo modello è stato partorito: sfuggire all'ingabbiatura inesorabile del Boolean model – costretto a emettere unicamente un verdetto asettico del tipo "tutto dentro o tutto fuori" – sostituendolo con una graduatoria sensibile (ranking) in cui i contenuti con un grado di pertinenza alto affiorino posizionandosi in cima rispetto alle letture marginali.

---

### Glossario / Concetti Chiave

- **Collection frequency vs Document frequency**: La Collection frequency conta tutte le ripetizioni assolute che un termine ha all'interno della collezione totale, tenendo in considerazione le occorrenze multiple nello stesso testo; la Document frequency valuta strettamente in quanti singoli e univoci documenti della collezione compaia quel preciso termine, rendendola l'indicatore principe per stabilire quanto una keyword sia distintiva.

- **TF-IDF Weighting**: Rappresenta in assoluto il più impiegato modello matematico di pesatura per i documenti. Moltiplica la Term frequency di una parola all'interno del singolo documento con la Inverse Document frequency della collezione intera, innalzando il valore della parola solo se questa si ripete spesse volte in un determinato documento ma raramente nel resto della libreria.

- **Modello Spaziale Vettoriale (Vector Space Model)**: Metodologia topologica in cui i singoli termini del vocabolario assumono la forma di dimensioni di uno spazio vettoriale. I documenti, e conseguentemente le query digitate dagli utenti, divengono punti (o vettori sparsi a dimensione $\mathbb{R}^{|V|}$) misurabili in questo spazio. Più i due vettori sono vicini fra loro, maggiore è la similarità per definire il ranking.

---

### I Limiti della Distanza Euclidea

Una volta rappresentati i documenti e le query come vettori, sorge la necessità di formalizzare matematicamente la loro prossimità. Un primo approccio intuitivo per risolvere il problema potrebbe essere l'utilizzo della **Distanza Euclidea**. Questa metrica calcola letteralmente la distanza fisica intercorrente tra i punti finali (le punte) dei due vettori analizzati. Purtroppo, affidarsi alla pura Distanza Euclidea si rivela una pessima idea. Il motivo principale del suo fallimento risiede nel fatto che questa metrica restituisce valori enormi quando viene applicata a vettori caratterizzati da lunghezze molto diverse tra loro.
[INSERIRE IMMAGINE: Grafico cartesiano bidimensionale con le parole GOSSIP e JEALOUS sugli assi x e y. Mostra i vettori d1, d3 e in particolare evidenzia con una linea tratteggiata rossa la grande distanza euclidea tra il vettore della query q e il vettore del documento d2] Come si evince osservando lo spazio vettoriale, la distanza tra il vettore della query $\vec{q}$ e il vettore del documento $\vec{d_2}$ risulta estremamente ampia, nonostante la distribuzione effettiva dei termini (ovvero l'argomento trattato) all'interno di entrambi sia molto simile. In definitiva, la Distanza Euclidea funziona correttamente solamente se applicata a vettori che sono stati precedentemente normalizzati.

### Sostituire la Distanza con l'Angolo

Per aggirare gli ostacoli posti dalla difformità di lunghezza dei testi, si abbandona la misurazione lineare per adottare l'angolo tra i vettori. A riprova di questa logica, si può ricorrere a un semplice esperimento mentale: prendiamo un documento qualsiasi $d$ e accodiamolo a se stesso per generare un nuovo documento che chiameremo $d'$. Da un punto di vista strettamente semantico, $d'$ e $d$ possiedono l'identico contenuto. Nello spazio vettoriale, le rappresentazioni di questi due documenti si sovrappongono perfettamente, con l'unica differenza che la magnitudine (lunghezza) di $d'$ risulta essere esattamente il doppio di quella di $d$. In questo scenario, la Distanza Euclidea tra i due restituirebbe un valore piuttosto elevato e fuorviante. Al contrario, l'angolo compreso tra le due rappresentazioni vettoriali dei documenti risulta pari a $0^{\circ}$. Da qui scaturisce un'idea chiave del Ranked Retrieval: bisogna ordinare i vettori dei documenti basandosi sull'"angolo" che essi formano in relazione al vettore della query.

### Dal Coseno al Prodotto Scalare

I concetti di ordinamento basato sull'angolo o basato sul coseno sono del tutto equivalenti. Ordinare i documenti in base all'ordine crescente dell'angolo compreso tra query e documento equivale a tutti gli effetti a ordinarli in ordine decrescente rispetto al loro **Coseno**, ovvero calcolando $cosine(query, document)$. In questa equivalenza logica, l'angolo funge da indice di "distanza", mentre il valore del coseno incarna la pura "similarità". Questa proprietà è dettata dal fatto che il coseno è una funzione monotonicamente decrescente per l'intervallo di gradi che va da $[0^{\circ}, 180^{\circ}]$.
[INSERIRE IMMAGINE: Grafico a onda che illustra l'andamento della funzione coseno, la quale parte da un valore di 1 e decresce gradualmente scendendo sotto lo zero nell'intervallo tra 0 e 180 gradi sull'asse delle ascisse] Per estrarre questo valore, si ricorre al concetto algebrico e geometrico di **Dot-product** (prodotto scalare). La sua definizione algebrica prevede la somma dei prodotti delle singole componenti: $A \cdot B = \sum_{i=1}^{n} A_i B_i = A_1 B_1 + A_2 B_2 + \dots + A_n B_n$. La sua definizione geometrica lo inquadra invece come $A \cdot B = ||A|| ||B|| [cite_start]\cos \theta$.
[INSERIRE IMMAGINE: Illustrazione geometrica di due vettori A e B divergenti che formano un angolo theta, evidenziando la proiezione ortogonale di A su B calcolata come magnitudine di A per il coseno di theta]
In questa equazione, la magnitudine (o lunghezza euclidea) del vettore A è determinata dalla **Norma $L_2$**, calcolata come $||A|| [cite_start]= \sqrt{A \cdot A}$. Isolando il parametro di nostro interesse, ricaviamo la formula finale per la similarità: $\cos(\theta) = \frac{A \cdot B}{||A|| [cite_start]\cdot ||B||} = \frac{A}{||A||} \cdot \frac{B}{||B||}$.

### Length Normalization e Cosine Similarity

Come introdotto dalla formula precedente, l'operazione che prevede di dividere un vettore per la sua Norma $L_2$ ($||\vec{x}||_2 = \sqrt{\sum_{i} x_i^2}$) prende il nome di **Length Normalization**. L'applicazione di questa divisione trasforma di fatto il vettore originario in un vettore unitario (di lunghezza intrinseca pari a 1), andandolo a proiettare idealmente sulla superficie esterna di un'ipersfera unitaria. Ritornando al nostro esempio teorico precedente, i due documenti fittizi $d$ e $d'$ avranno vettori totalmente identici a seguito della normalizzazione della lunghezza. Questa operazione è vitale in ambito IR perché permette a documenti lunghi e documenti brevi di disporre finalmente di pesi paragonabili tra loro in maniera equa. La **Cosine Similarity**, denotata come $\cos(\vec{q}, \vec{d})$, è quindi il calcolo del coseno dell'angolo tra il vettore query e il vettore documento. Sviluppando l'intera formula con i pesi TF-IDF, otteniamo: $\cos(\vec{q}, \vec{d}) = \frac{\vec{q} \bullet \vec{d}}{|\vec{q}||\vec{d}|} = \dots = \frac{\sum_{i=1}^{|V|} q_i d_i}{\sqrt{\sum_{i=1}^{|V|} q_i^2} \sqrt{\sum_{i=1}^{|V|} d_i^2}}$. In questa equazione, $q_i$ rappresenta il peso TF-IDF del termine $i$ all'interno della query, mentre $d_i$ è il corrispettivo peso del termine $i$ calcolato all'interno del documento analizzato. Se operiamo alla base con vettori già pre-normalizzati per lunghezza (unitari), il denominatore diventa automaticamente 1, e la Cosine Similarity si semplifica magistralmente coincidendo in tutto e per tutto con il puro prodotto scalare: $\cos(\vec{q}, \vec{d}) = \vec{q} \bullet \vec{d} = \sum_{i=1}^{|V|} q_i d_i$.
[INSERIRE IMMAGINE: Grafico cartesiano bidimensionale delimitato dagli assi POOR e RICH su una scala da 0 a 1. All'interno si osserva la curva tratteggiata di un'ipersfera unitaria lungo la quale si attestano le estremità dei vettori normalizzati relativi a tre documenti (d1, d2, d3) e a una query (q), con l'angolo theta indicato in evidenza]

### Document Clustering: Un Esempio su Tre Documenti

Le medesime logiche di similarità tramite coseno applicate al rapporto query/documento si rivelano efficaci per comparare la vicinanza intercorrente tra multipli documenti fra di loro, uno scopo tipicamente ricollegato alle operazioni di **Document Clustering**. Per chiarire l'implementazione numerica del concetto, proviamo a calcolare quanto siano simili fra di loro tre celebri romanzi della letteratura inglese classica. I testi in esame sono:

- **SaS**: *Sense and Sensibility* (scritto da Jane Austen, 1775-1817)

- **PaP**: *Pride and Prejudice* (della medesima Jane Austen)

- **WH**: *Wuthering Heights* (capolavoro di Emily J. Brontë, 1818-1848).
  [INSERIRE IMMAGINE: Ritratto affiancato di due donne d'epoca, storicamente identificabili come le scrittrici Jane Austen e Emily J. Brontë]

Analizziamo a tal fine le "Term frequencies", ovvero i meri conteggi quantitativi delle occorrenze estratti dalle opere per quattro peculiari parole chiave prese a campione:

| **term**  | **SaS** | **PaP** | **WH** |
| --------- | ------- | ------- | ------ |
| affection | 115     | 58      | 20     |
| jealous   | 10      | 7       | 11     |
| gossip    | 2       | 0       | 6      |
| wuthering | 0       | 0       | 38     |

Attraverso i dati non normalizzati di questa tabella, combinati con la successiva pesatura logaritmica delle frequenze e relativa normalizzazione della lunghezza, il motore sarà in grado di stabilire gli angoli geometrici che separano i tre romanzi all'interno dello spazio vettoriale, aggregando tra di loro le opere oggettivamente più affini.

---

### Glossario / Concetti Chiave

- **Distanza Euclidea vs Distanza Angolare**: La prima calcola lo spazio tra le estremità di due vettori risultando inefficiente a causa delle diverse magnitudini dei testi non normalizzati; la seconda stima la prossimità osservando l'angolo formato tra i vettori alla loro origine spaziale.

- **Cosine Similarity**: Funzione trigonometrica utilizzata in Retrieval per tramutare l'ampiezza dell'angolo in un gradiente di "similarità". Più l'angolo è stretto, più il suo coseno si avvicina al valore 1 garantendo una corrispondenza semantica.

- **Length Normalization**: Operazione matematica che prevede la divisione di ogni coordinata di un vettore per la sua magnitudine totale (Norma L2), equiparando vettori intrinsecamente corti a quelli lunghi trasformandoli tutti in vettori unitari.

- **Prodotto Scalare (Dot-product)**: Operazione algebrica fondamentale che, qualora applicata a vettori assoggettati a Length Normalization preventiva, coincide esattamente con il punteggio della Cosine Similarity, rendendo la sua formula rapida da processare a livello computazionale.

---

### Il Calcolo Pratico della Cosine Similarity (Continuazione)

Riprendendo l'esempio del **Document Clustering** relativo ai tre romanzi classici (*Sense and Sensibility*, *Pride and Prejudice*, *Wuthering Heights*), è possibile osservare matematicamente come le pure frequenze si trasformino in vettori normalizzati. In un primo momento, i conteggi grezzi vengono smorzati attraverso la **Log frequency weighting**. La matrice ottenuta assume questi valori:

| **term**  | **SaS** | **PaP** | **WH** |
| --------- | ------- | ------- | ------ |
| affection | 3.06    | 2.76    | 2.30   |
| jealous   | 2.00    | 1.85    | 2.04   |
| gossip    | 1.30    | 0       | 1.78   |
| wuthering | 0       | 0       | 2.58   |

Successivamente, ai vettori generati viene applicata la **Length normalization** (normalizzazione della lunghezza), in modo da parificare il peso delle opere a prescindere dal numero totale delle loro pagine. I vettori unitari definitivi diventano:

| **term**  | **SaS** | **PaP** | **WH** |
| --------- | ------- | ------- | ------ |
| affection | 0.789   | 0.832   | 0.524  |
| jealous   | 0.515   | 0.555   | 0.465  |
| gossip    | 0.335   | 0       | 0.405  |
| wuthering | 0       | 0       | 0.588  |

A questo punto, avendo a disposizione vettori normalizzati, il calcolo della similarità si riduce al semplice prodotto scalare. Moltiplicando e sommando le componenti, i risultati indicano che $cos(SaS, PaP) \approx 0.789 \times 0.832 + 0.515 \times 0.555 + 0.335 \times 0.0 + 0.0 \times 0.0 \approx 0.94$. Eseguendo la stessa operazione per le altre coppie, otteniamo $cos(SaS, WH) \approx 0.79$ e $cos(PaP, WH) \approx 0.69$. Di conseguenza, la classifica finale di affinità decreta che $cos(SaS, PaP) > cos(SaS, WH) > cos(PaP, WH)$, dimostrando, come prevedibile, che i due testi scritti dallo stesso autore sono oggettivamente i più simili tra loro.

### Le Varianti del Punteggio e la Notazione SMART

Il concetto di pesatura TF-IDF non è un dogma fisso, ma un'infrastruttura adattabile. Esistono innumerevoli varianti, che differiscono in base a come viene calcolata la Term Frequency (ad esempio introducendo logaritmi o booleani), a come si gestisce la Document Frequency e, infine, al metodo scelto per la normalizzazione. Per mappare agilmente queste varianti, i sistemi di Information Retrieval fanno uso della **Notazione SMART**. La tabella seguente sintetizza le sigle standardizzate:

| **Term frequency**                                                  | **Document frequency**                         | **Normalization**                                   |
| ------------------------------------------------------------------- | ---------------------------------------------- | --------------------------------------------------- |
| n (natural) $tf_{t,d}$                                              | n (no) 1                                       | n (none) 1                                          |
| l (logarithm) $1+log(tf_{t,d})$                                     | t (idf) $log\frac{N}{df_t}$                    | c (cosine) $\frac{1}{\sqrt{w_1^2+w_2^2+...+w_M^2}}$ |
| a (augmented) $0.5+\frac{0.5\times tf_{t,d}}{max_t(tf_{t,d})}$      | p (prob idf) $max\{0,log\frac{N-df_t}{df_t}\}$ | u (pivoted unique) $1/u$                            |
| b (boolean) $1$ if $tf_{t,d}>0$, $0$ otherwise                      |                                                | b (byte size) 1/CharLength, $\alpha<1$              |
| L (log ave) $\frac{1+log(tf_{t,d})}{1+log(ave_{t\in d}(tf_{t,d}))}$ |                                                |                                                     |

La convenzione in uso nei motori di ricerca prevede di unire queste lettere nel formato **ddd.qqq**, dove le prime tre lettere ("ddd") specificano le scelte operate per i documenti del set, mentre le ultime tre ("qqq") indicano il trattamento riservato alla query immessa dall'utente. Uno schema operativo standard e diffusissimo è identificato dalla sigla **lnc.ltc**. Questa dicitura impone che il documento ("lnc") venga elaborato con un logaritmo per la TF ('l'), nessuna IDF ('n') e normalizzato tramite coseno ('c'). Per contro, la query ("ltc") subirà un approccio logaritmico alla TF ('l'), integrerà il fattore IDF in fase di ricerca ('t') e verrà anch'essa normalizzata con il coseno ('c').

Per esemplificare lo schema lnc.ltc, supponiamo di avere un documento contenente i termini "car insurance auto insurance" e una query "best car insurance". Il calcolo produrrà dei vettori pre-normalizzati con una lunghezza vettoriale (Doc length) stimabile in $\sqrt{1^2+0^2+1^2+1.3^2} \approx 1.92$. Il punteggio definitivo ("Score"), calcolato sommando i prodotti finali tra query e documento, si assesterà sul valore di $0 + 0 + 0.27 + 0.53 = 0.8$.

### Sintesi del Vector Space Ranking

Ricapitolando il funzionamento pratico del Vector Space Ranking, il flusso procedurale si può dividere in passi chiarissimi:

1. Rappresentare la query dell'utente come un vettore ponderato TF-IDF.

2. Modellare ogni singolo documento dell'archivio come un vettore ponderato TF-IDF.

3. Calcolare lo score di "Cosine similarity" confrontando il vettore della query con ciascun vettore documentale.

4. Ordinare i documenti con rispetto alla query partendo dal punteggio più alto.

5. Mostrare a schermo solamente i "Top K" (es. $K=10$) documenti all'utente.

### L'evoluzione del Ranking: Best Match 25 (Okapi BM25)

Il logico successore del classico TF-IDF vettoriale è il modello **Best Match 25**, universalmente noto come **Okapi BM25**. Esso non è altro che una funzione di ranking basata su teorie probabilistiche di recupero delle informazioni e sul comportamento utente (information-seeking behaviour). È stato sviluppato a cavallo tra gli anni '80 e '90 nell'ambito del sistema sperimentale Okapi presso il Centre for Interactive Systems Research del dipartimento di Information Science della City University di Londra. Attualmente, il BM25 è considerato una baseline robusta ed è lo standard de facto integrato quasi ovunque. Troviamo riferimenti importanti in accademia ad opera di ricercatori come Stephen Robertson e Hugo Zaragoza (2009).

La complessa formula algoritmica di base del modello BM25 definisce lo score tra il documento $D$ e la query $Q$ nel modo seguente:

$$score(D,Q) = \sum_{i=1}^{n} IDF(q_i) \cdot \frac{f(q_i, D) \cdot (k_1 + 1)}{f(q_i, D) + k_1 \cdot (1 - b + b \cdot \frac{|D|}{avgdl})}$$

Affiancata dal calcolo probabilistico per il fattore Inverse Document Frequency:

$$IDF(q_i) = \ln\left(\frac{N - n(q_i) + 0.5}{n(q_i) + 0.5} + 1\right)$$

All'interno di queste equazioni figurano variabili cruciali:

- **$|D|$**: La cardinalità o lunghezza del singolo documento espressa in numero di parole.

- **$avgdl$** (o $L$): La lunghezza media dei documenti nell'intera collezione.

- **$N$**: Il numero totale dei documenti.

- **$n(q_i)$**: Il parametro di document-frequency per l'i-esimo termine della query.

- **$b$** e **$k_1$**: Iper-parametri di taratura. Tipicamente, se non ottimizzati nello specifico, $b$ si colloca nell'intervallo $[0, 1]$ (spesso fissato a 0.75), mentre $k_1$ assume un valore tra $[1.2, 2.0]$.

### Normalizzazione e Saturazione nel Modello BM25

Ciò che distingue il BM25 dagli schemi precedenti sono due meccanismi: la normalizzazione pivotata e la saturazione della frequenza.

La **Pivoted length normalization** introduce un normalizzatore al denominatore della formula, basato sul rapporto tra la lunghezza del testo in esame e la media globale ($avgdl$). Essendo modellata dal parametro $b \in [0, 1]$, se viene assegnato un valore $b > 0$, il normalizzatore assumerà proporzioni superiori a 1.0 per tutti quei documenti la cui lunghezza supera la media del set. Il fine ultimo di questo espediente è penalizzare deliberatamente i documenti troppo lunghi, compensando il fatto che testi estremamente estesi presentano una naturale inclinazione statistica ad accoppiarsi erroneamente e a far match con qualunque query immessa. I testi più corti vengono invece ricompensati.
[INSERIRE IMMAGINE: Grafico cartesiano della "Pivoted length normalization", in cui le linee mostrano l'effetto del parametro 'b'. Documenti più lunghi della media subiscono una penalizzazione (linee discendenti o normalizzatore > 1), mentre quelli più corti ottengono un premio (Reward)]

Infine, la porzione della formula $\tau(F_t) = \frac{F_t}{k + F_t}$ funge da **funzione di saturazione** (Saturation function). Il suo compito essenziale è modellare matematicamente la non-linearità del contributo apportato dalla Term Frequency, affiancata alla sopracitata normalizzazione documentale. Mentre nei modelli TF classici il peso aumenta inesorabilmente (eccesso di fiducia per keyword ripetute ad libitum), nel modello BM25 il peso derivante dalla frequenza cresce inizialmente in modo ripido per poi appiattirsi (saturarsi) all'aumentare esponenziale delle occorrenze. Questo limite asintotico scongiura manipolazioni dei risultati (come il Keyword Stuffing) e raffina notevolmente l'equità del motore di ricerca.
[INSERIRE IMMAGINE: Grafico a linee curve comparativo: mostra la progressione illimitata in salita del 'Classic TF score' a confronto con la curva del 'BM25 TF Score', che si appiattisce rapidamente creando un tetto massimo di saturazione al crescere della Term Frequency]

---

### Glossario / Concetti Chiave

- **Notazione SMART (ddd.qqq)**: Sintassi contratta utilizzata in accademia e nell'industria dell'IR per indicare univocamente quali varianti di pesatura e di normalizzazione sono state assegnate ai documenti analizzati (primo blocco ddd) e alla query ricercata (secondo blocco qqq).

- **BM25 (Best Match 25)**: Funzione probabilistica di ranking sviluppata dall'Università di Londra, diventata baseline assoluta nel recupero testuale grazie alla sua gestione intelligente e bilanciata delle frequenze.

- **Pivoted Length Normalization**: Componente del BM25 progettato esplicitamente per infliggere malus aritmetici ai testi estremamente prolissi, ridimensionando i vantaggi statistici che documenti enormi maturano casualmente intercettando parole chiave spurie.

- **Funzione di Saturazione**: Principio logico alla base del BM25 per il quale la ripetizione insistente di un termine si blocca progressivamente verso un tetto massimo di "peso", prevenendo l'accumulo sproporzionato di rilevanza nei conteggi altissimi di Term Frequency.

---

# Slide 4: Indicizzazione di una Collezione di Documenti

L'indicizzazione è un processo fondamentale nell'Information Retrieval per organizzare ed estrarre informazioni da grandi collezioni di dati testuali in maniera efficiente. In questo capitolo verranno esplorati i fondamenti teorici e pratici dell'elaborazione delle query, partendo dall'architettura base del sistema fino all'analisi matematica del peso dei termini nei documenti.

### Architettura del Sistema di Retrieval

L'architettura di un moderno motore di ricerca si divide concettualmente in due fasi temporali distinte: la fase **Offline** e la fase **Online**. Questa netta separazione è essenziale perché l'indicizzazione richiede molto tempo e deve essere preparata prima che l'utente effettui una ricerca, che invece deve avvenire quasi istantaneamente.

Durante la fase offline, il sistema si basa su una **Document Collection** (collezione di documenti) per generare le strutture dati primarie. Attraverso il processo di **Indexing** si costruisce l'**Inverted Index** (indice invertito), che mapperà i futuri termini ricercati direttamente ai documenti in cui compaiono. Parallelamente, un modulo chiamato **Feature Processor** estrae caratteristiche aggiuntive dei documenti e le salva all'interno del **Document Features Repository**. Infine, utilizzando un insieme di **Training Data**, la fase di **Training** provvede ad addestrare un modello algoritmico (**Learning-to-rank Model**) che sarà poi usato per l'ordinamento dei risultati.

Nel momento in cui inizia la fase online, l'utente inserisce una **Query**. Questa stringa viene processata dal modulo di **Expanded Query** per arricchirla di termini semanticamente utili, per poi passare al vero e proprio **Query Processing**. Questo componente interroga l'Inverted Index creato precedentemente. I risultati preliminari vengono poi uniti alle caratteristiche estratte durante il passaggio di **Feature Lookup and Computation**. A questo punto, il sistema sfrutta la **Learned Ranking Function** per riordinare i documenti in base alla loro rilevanza e mostrarli all'utente, ad esempio sulla tipica interfaccia di Google.

[INSERIRE IMMAGINE: Diagramma di flusso dell'architettura di Information Retrieval che mostra i passaggi Offline (Indexing, Feature Processor, Training) e i passaggi Online (Query, Query Processing, Feature Lookup, Learned Ranking Function) in collegamento fra loro.]

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

[INSERIRE IMMAGINE: Rappresentazione visiva della moltiplicazione tra il vettore della Query e la matrice sparsa Termini-Documenti per la valutazione della query]

Dal punto di vista concettuale, la valutazione di una query in questo spazio vettoriale equivarrebbe a moltiplicare il vettore della query per l'intera matrice Termini-Documenti. Tuttavia, operare direttamente su questa matrice comporta due gravissimi problemi di ottimizzazione. In primo luogo, c'è una forte inefficienza spaziale: la matrice risultante è gigantesca ma estremamente sparsa, ovvero composta prevalentemente da zeri, sprecando enormi quantità di memoria. In secondo luogo, c'è un'altrettanto grave inefficienza temporale: per calcolare i risultati bisognerebbe processare l'intera collezione di documenti in tempo reale (online) durante la ricerca dell'utente, un'operazione insostenibile per un sistema reattivo.

### L'Indice Invertito per il Boolean Retrieval

Per superare i limiti di calcolo della matrice, i motori di ricerca si affidano all'**Inverted Index** (Indice Invertito). Si tratta di una struttura dati che memorizza una mappatura diretta che va dai termini alle loro rispettive occorrenze all'interno dell'insieme di documenti. Questo strumento rappresenta il componente centrale di qualsiasi algoritmo di indicizzazione in un motore di ricerca tipico. L'obiettivo primario dell'indice invertito è ottimizzare al massimo la velocità della query, permettendo di trovare istantaneamente i documenti in cui compare un set specifico di parole.

[INSERIRE IMMAGINE: Struttura dell'Indice Invertito per Boolean Retrieval, con un Dictionary sulla sinistra che mappa le parole verso le Posting Lists sulla destra contenenti gli ID dei documenti]

Architetturalmente, l'indice invertito è composto da due parti. Da un lato c'è il **Dictionary** (Dizionario), che contiene l'elenco dei termini estratti dalla collezione. Dall'altro lato ci sono le **Posting Lists** (Liste di Registrazione), che per ogni termine del dizionario contengono semplicemente gli identificativi (ID) dei documenti in cui quel termine è presente. Ad esempio, un termine comune come "a" punterà a una Posting List molto lunga (es. documenti 1, 2, 3), mentre parole più specifiche avranno liste più corte.

### Adattare l'Indice Invertito per Modelli Avanzati (BM25)

Mentre la struttura di base con soli ID dei documenti è sufficiente per il Boolean Retrieval puro, modelli di ranking più sofisticati necessitano di ulteriori dati quantitativi. Quando si costruiscono indici invertiti per algoritmi avanzati come il **BM25**, la struttura si arricchisce in modo significativo.

[INSERIRE IMMAGINE: Struttura dell'Indice Invertito per BM25. Il dizionario mostra i valori extra di document frequency e collection frequency, mentre le posting list mostrano l'ID del documento accoppiato alla frequenza del termine all'interno di esso]

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

[INSERIRE IMMAGINE: Diagramma visivo del processo TAAT che elabora prima la posting list del termine "information" e poi quella di "retrieval", mostrando la generazione e l'aggiornamento numerico degli accumulatori associati ai DocId.]

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

Attraverso l'uso di queste tabelle, il TAAT è in grado di supportare la complessa logica delle query. L'algoritmo può tracciare in memoria la presenza condivisa dei termini per risolvere le intersezioni logiche richieste dall'operatore AND, oppure unire i risultati espandendoli per l'operatore OR . Allo stesso modo, questa memoria temporanea viene interrogata per escludere specifici documenti in presenza di un operatore NOT, o ancora per sommare progressivamente pesi matematici molto più complessi qualora si utilizzi un modello di ranking avanzato come il BM25.

[INSERIRE IMMAGINE: Diagramma visivo dell'elaborazione TAAT che mostra le liste di "information" e "retrieval" collegate, tramite una Direct Access Table o Hash Table, ai rispettivi accumulatori in cui si sommano i valori per calcolare operatori come AND, OR, NOT e BM25.]

### Pro e Contro del Modello Term-at-a-time

L'approccio TAAT offre dei vantaggi e degli svantaggi ben precisi. Tra i suoi pro principali vi è sicuramente un'estrema facilità di implementazione a livello algoritmico . Al sistema basta infatti utilizzare una semplice operazione di base, la funzione `next()`, per scorrere linearmente e senza interruzioni la Posting List fino alla sua conclusione.

Tuttavia, i difetti di questa tecnica si fanno sentire pesantemente sulle performance dell'hardware e sull'efficienza di ricerca. Il contro più impattante è l'enorme quantità di "cache misses" (mancanze nella cache) causata dal vasto numero di accumulatori in uso. Poiché l'algoritmo deve aggiornare punteggi sparsi saltando continuamente da un punto all'altro della memoria, il processore fatica a mantenere i dati pronti all'uso. Inoltre, emerge un limite logico critico: il TAAT non offre alcuna possibilità di saltare ("skipping") gli identificativi dei documenti (DocIds) nel caso di query altamente selettive, come quelle che utilizzano l'operatore AND. L'algoritmo è costretto a leggere e processare tutto, perdendo tempo su documenti che potrebbero essere scartati a priori.

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

# Slide 5:
