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

# Slide 5:# Compressione di Dati nei Motori di Ricerca Moderni

Nei moderni sistemi di Information Retrieval, le dimensioni delle collezioni di documenti richiedono soluzioni architetturali e algoritmiche avanzate. In questo capitolo esploreremo i fondamenti della compressione dei dati, partendo dalla struttura generale di un motore di ricerca fino ad arrivare ai dettagli matematici dei primi codificatori di interi basati sulla rappresentazione binaria.

### L'Architettura e l'Elaborazione delle Query

L'architettura di un motore di ricerca moderno prevede una netta separazione tra le operazioni eseguite offline e quelle online. Durante la fase offline, il sistema si occupa dell'indicizzazione della collezione di documenti per generare l'**Inverted Index** (indice invertito). Parallelamente, le feature dei documenti vengono elaborate ed estratte in un repository dedicato, mentre i dati di addestramento nutrono un modello di **Learning-to-rank**.

Nella fase online, il flusso parte dalla query dell'utente, che viene prima espansa e poi passata al sistema di processamento delle query. I risultati parziali interrogati sull'indice invertito passano al blocco di recupero e calcolo delle feature, per essere infine ordinati dalla funzione di ranking appresa e restituiti sotto forma di risultati di ricerca finali. L'infrastruttura sottostante a questo ecosistema è formata da immense server farm che contengono e processano l'indice e i dati di training.

[INSERIRE IMMAGINE: Diagramma dell'architettura di un motore di ricerca, divisa in blocchi logici online e offline, mostrando il flusso dalla query dell'utente fino alla Learned Ranking Function e ai database sottostanti]

### La Necessità dello Sharding

Per gestire moli di dati così imponenti e garantire un'elevata reattività alle query, i motori di ricerca si affidano a una tecnica chiamata **Sharding**. Lo sharding permette di distribuire i dati frammentati su molteplici nodi fisici anziché mantenerli su un unico server centrale. L'adozione di questa tecnica è spinta da tre necessità fondamentali:

In primo luogo, garantisce la **scalabilità**: man mano che la collezione di documenti cresce, una singola macchina non è più in grado di gestire il carico. Lo sharding permette al motore di scalare orizzontalmente tramite la semplice aggiunta di nuovi nodi. In secondo luogo, migliora nettamente le **performance**. Distribuendo il carico su più nodi, si alleggerisce il lavoro di ogni singolo server, risultando in una maggiore velocità di esecuzione delle query e prestazioni complessive superiori. Infine, aumenta la **tolleranza ai guasti** (Fault Tolerance). Utilizzando copie di sicurezza chiamate **repliche**, i dati restano sempre disponibili attraverso altri nodi anche qualora un server dovesse subire un guasto.

Di conseguenza, se l'indice invertito dovesse allargarsi, si dovranno incrementare gli **shard** (frammenti di dati). Se ad aumentare è invece il traffico di interrogazioni da parte degli utenti, sarà necessario aumentare il numero delle **repliche**. In ambedue gli scenari, la scalabilità viene raggiunta aumentando le macchine fisiche a disposizione.

[INSERIRE IMMAGINE: Struttura di rete dello Sharding: una query viene ricevuta da un broker che smista la richiesta a varie repliche parallele, le quali contengono i diversi shard ($s_1, \dots, s_k$) all'interno dei server indice]

### Il Modello Base della Compressione e lo Space-Time Tradeoff

La compressione dei dati si fonda su un modello concettuale basilare: una stringa di bit in ingresso, definita $B$, viene elaborata da un algoritmo **Compressore** che la riduce, restituendo una stringa compressa in uscita denominata $C(B)$. L'operazione opposta è ovviamente affidata a un **Decompressore**. In base alle necessità, questa tecnologia può essere *lossy* (ammettendo una certa perdita di informazioni) o *lossless* (senza perdita di dettagli). Il metro di valutazione primario dell'algoritmo è il **rateo di compressione**, calcolato tramite la formula $CR = \frac{|B|}{|C(B)|}$. Se il valore ottenuto è $CR = r$, significa che lo spazio occupato dall'output compresso $|C(B)|$ è $r$ volte inferiore rispetto alla dimensione dell'input originale $|B|$. Applicazioni storiche e comuni di questi principi includono utility da riga di comando come *gzip* e *bzip2*.

Questi algoritmi sono strettamente legati a un compromesso architetturale, noto come **Space-Time Tradeoff**. Il rateo di compressione finale dipende infatti dalla velocità desiderata per le operazioni di compressione e decompressione, le quali impattano sul consumo energetico e sulla potenza di calcolo richiesta alla CPU. Spesso occorre bilanciare lo spazio di archiviazione risparmiato dalla struttura dati compressa e l'efficienza delle operazioni che vi devono essere eseguite sopra. Non a caso, tool come *gzip* offrono 9 livelli di compressione distinti, dove il livello 1 è estremamente rapido ma offre risultati di compressione peggiori, mentre il livello 9 restituisce la miglior compressione al costo di tempistiche di calcolo più dilatate. Nel panorama attuale dell'Information Retrieval, questo tradeoff assume un'importanza capitale: non è più tollerabile l'ingenuo paradigma del "decomprimi prima di calcolare". L'obiettivo finale dei sistemi moderni è quello di poter effettuare operazioni di computazione operando direttamente sui dati già compressi.

### Codifica di Interi e l'Ottimizzazione dei D-Gaps

Scendendo nello specifico dell'indicizzazione, ci troviamo davanti al problema dei codificatori di interi: dato un intero $x > 0$, è necessario progettare un codice capace di rappresentare quel numero usando meno bit possibili. La stringa di bit risultante in uscita è definita **codeword** di $x$, indicata con $C(x)$. Elaborando un'intera sequenza $S$ di $n$ interi, la codifica finale avviene concatenando linearmente i singoli codeword: $C(x_1) \cdot\cdot\cdot C(x_n)$. I codici che si analizzano inizialmente sono definiti **statici**, poiché assegnano immancabilmente la stessa codeword a un determinato intero a prescindere dalla specifica sequenza processata.

Nel contesto delle Posting List (che memorizzano ad esempio gli ID dei documenti in cui appare il termine "information" disposti in ordine crescente), si sfrutta una particolare tecnica denominata **d-gaps** per pre-trattare i dati. Anziché salvare numeri via via più grandi, i d-gaps permettono di salvare semplicemente la differenza numerica tra la cella attuale e la cella precedente. Di conseguenza, una lista come $[1, 5, 8, 11]$ viene ridotta agli scarti $[1, 4, 3, 3]$, mantenendo valori molto più piccoli e per natura più facili da comprimere.

### La Rappresentazione Binaria e il Fallimento dell'Ambiguità (Epic Fail)

Un primo approccio alla scrittura in bit dei d-gaps potrebbe essere l'utilizzo di una stringa binaria di lunghezza fissa. Sappiamo che $k$ bit sono in grado di rappresentare efficacemente qualsiasi intero nell'intervallo $0 \le x < 2^k$. Definendo $bin(x)$ come la pura rappresentazione binaria di $x$, il numero di bit necessari per formarla è pari a $\lceil log_{2}(x+1)\rceil$ bit. Avendo stabilito di lavorare con interi strettamente positivi ($x > 0$), la codeword basilare viene indicata come $B(x) = bin(x-1)$. Questo definisce formalmente un limite matematico inferiore: la grandezza di un codeword generico $C(x)$ deve sempre sottostare alla disequazione $|C(x)| > \lceil log_{2}(x)\rceil = |bin(x-1)| [cite_start]= |B(x)|$.

Possiamo osservare la tabella delle associazioni elementari per i primi numeri:

| **Valore X** | **Codeword B(x)** |
| ------------ | ----------------- |
| 1            | 0                 |
| 2            | 1                 |
| 3            | 10                |
| 4            | 11                |
| 5            | 100               |
| 6            | 101               |
| 7            | 110               |
| 8            | 111               |

Tuttavia, provando a concatenare linearmente le codifiche risultanti dai valori d-gaps di una lista testuale, emerge un errore architetturale fatale definito come un vero e proprio "Epic Fail". Poiché i codeword binari hanno lunghezze disuguali e nessuna indicazione interna di troncamento, la sequenza diviene un agglomerato privo di spaziature logiche, come la stringa `0111010111011101001`.

[INSERIRE IMMAGINE: Grafo ad albero rovesciato che mostra un esempio di decodifica ambigua in cui la stringa binaria può generare sia la corretta sequenza [1, 4, 3...] che un'errata sequenza alternativa [1, 8, 1...]]

Al momento della decompressione, l'algoritmo non è più in grado di ripristinare in modo certo la lista d'origine e genererà risultati contraddittori e ambigui. La natura di questa ambiguità è data dal fatto che determinati codeword fungono accidentalmente da prefisso per codeword più lunghe appartenenti ad altri valori. Per risolvere questo difetto strutturale e operare in totale sicurezza, i motori di ricerca si affidano esclusivamente a codici univocamente decodificabili in cui vige la proprietà **Prefix-free**: in questi sistemi non esistono mai situazioni in cui un codeword sia l'esatto inizio (prefisso) di un codeword più lungo.

---

### Glossario e Concetti Chiave

- **Sharding:** Una tecnica basilare dei moderni motori di ricerca volta a frammentare e distribuire i dati attraverso molti nodi fisici, essenziale per scalabilità orizzontale, alte performance e tolleranza ai guasti.

- **Rateo di Compressione (CR):** Indicatore fondamentale dell'efficacia di un compressore, derivato dalla proporzione matematica tra la dimensione in bit in input ($|B|$) e la sua compressione in output ($|C(B)|$).

- **D-Gaps:** Un accorgimento pratico usato sulle liste di elementi crescenti (come le Posting List) che non codifica il numero in sé, bensì l'intervallo o la differenza rispetto all'elemento antecedente, abbassando il valore assoluto degli interi.

- **Codice Ambiguo:** Un paradigma di compressione malformato dove, in assenza di un rigido formato prefix-free, diviene impossibile stabilire una separazione netta e deterministica delle sequenze decodificabili.

---

# Codifiche Prefix-Free e Compressione di Liste

In questa sezione approfondiremo i criteri per rendere i codici binari decodificabili in modo univoco e analizzeremo diverse tecniche di compressione per interi singoli e per intere liste, valutandone l'efficienza rispetto ai limiti teorici.

### Decodificabilità Univoca e Codici Prefix-Free

Il problema principale riscontrato con la codifica binaria semplice è l'ambiguità: un codice può risultare un prefisso di un altro, rendendo impossibile distinguere i singoli numeri in una sequenza continua di bit. Per risolvere questo limite, ci concentriamo sui **codici prefix-free** (o codici a prefisso), definiti come codici in cui nessuna codeword è il prefisso di un'altra. Questa proprietà garantisce che, durante la lettura del flusso di bit, non appena viene riconosciuta una sequenza corrispondente a un simbolo, essa possa essere decodificata immediatamente senza incertezze. L'obiettivo fondamentale è dunque progettare codici decodificabili il cui ingombro in termini di spazio sia il più vicino possibile alla rappresentazione binaria teorica $|B(x)|$.

### Il Codice Unario

La tecnica più semplice per garantire la proprietà prefix-free è il **codice unario**. L'idea alla base è quella di utilizzare il bit $1$ per rappresentare i dati e il bit $0$ come delimitatore di fine numero. Formalmente, un intero $x > 0$ viene rappresentato come una sequenza di $x-1$ volte il bit $1$, seguita da uno $0$, indicata come $U(x) = 1^{x-1}0$. Di conseguenza, la lunghezza del codice è pari al valore stesso dell'intero ($|U(x)| = x$). Sebbene sia molto inefficiente per numeri grandi, il codice unario rappresenta un elemento costruttivo essenziale per codifiche più complesse ed è estremamente efficace per piccoli interi.

### Codifiche di Elias: Gamma e Delta

Per migliorare l'efficienza su interi più grandi, si utilizzano le codifiche di Elias, che rendono la rappresentazione binaria $bin(x)$ decodificabile anteponendo informazioni sulla sua lunghezza. La **codifica Elias Gamma** ($\gamma$) rappresenta la lunghezza della stringa binaria di $x$ utilizzando il codice unario, seguita dalla rappresentazione binaria di $x$ privata del bit più significativo (che è sempre $1$ per $x > 0$ e quindi ridondante). Ad esempio, per codificare il numero $6$, la cui rappresentazione binaria è $110$ (lunghezza $3$), scriveremo il $3$ in unario ($110$) e aggiungeremo i restanti bit del binario ($10$), ottenendo $11010$.

La **codifica Elias Delta** ($\delta$) segue un principio simile ma cerca di comprimere ulteriormente l'informazione sulla lunghezza. Invece di usare il codice unario, la lunghezza $|bin(x)|$ viene a sua volta codificata utilizzando Elias Gamma. Anche in questo caso, si aggiunge poi la parte finale di $bin(x)$ senza il bit più significativo. Questa struttura gerarchica rende la codifica Delta più vantaggiosa della Gamma per interi molto elevati.

### Variable Byte Code

Un approccio differente è quello del **Variable Byte (VB) code**, che privilegia la velocità di esecuzione e la semplicità di implementazione allineando le codeword ai byte anziché ai singoli bit. In questo sistema, ogni byte è diviso in due parti: $7$ bit sono dedicati alla rappresentazione dei dati dell'intero $x$, mentre l'ultimo bit (chiamato bit di controllo o di continuazione) segnala se la sequenza continua nel byte successivo o se il numero termina lì. Se l'intero richiede più di $7$ bit, viene distribuito su più byte; il bit di controllo sarà impostato a $1$ per tutti i byte tranne l'ultimo, che avrà lo $0$ come terminatore. Sebbene questo possa comportare un leggero spreco di spazio rispetto a codifiche bit-aligned, la velocità di accesso ai dati è notevolmente superiore.

### Compressione di Liste e Limite Inferiore Combinatorio

Quando passiamo dalla codifica di singoli interi a quella di intere liste strettamente crescenti $L = [x_1, \dots, x_n]$ (dove $x_n \le U$, con $U$ dimensione dell'universo), possiamo sfruttare le regolarità dell'intera sequenza per ottimizzare lo spazio. Una pratica comune è trasformare la lista in una sequenza di **d-gaps**, ovvero le differenze tra elementi consecutivi, rendendo i valori mediamente molto più piccoli e quindi più comprimibili.

Esiste un limite teorico alla compressione di queste liste, noto come **Limite Inferiore Combinatorio**. Poiché esistono $\binom{U}{n}$ modi diversi di scegliere $n$ interi distinti in un universo di dimensione $U$, il numero minimo di bit necessari per rappresentare una lista qualsiasi è dato da $\lceil \log_2 \binom{U}{n} \rceil$. Utilizzando l'approssimazione di Stirling, questo valore è circa $n \log_2(U/n) + 1.44n$ bit.

### Regolarità e Distribuzione dei Gap

L'efficienza reale di un compressore dipende da quanto riesce a superare il limite inferiore sfruttando le regolarità dei dati. Una sequenza che presenta valori molto raggruppati (**clustered**) è molto più comprimibile di una sequenza con distribuzione casuale, anche se hanno la stessa lunghezza $n$ e lo stesso universo $U$.

Analizzando collezioni reali come Gov2 o ClueWeb09, si osserva che la distribuzione dei gap non è uniforme: una percentuale altissima di gap ha dimensione $1$ (circa il $65\%$ in alcuni casi), e la frequenza diminuisce drasticamente all'aumentare della dimensione del gap. I moderni algoritmi di compressione sono progettati specificamente per sfruttare questa alta frequenza di gap piccoli per abbattere drasticamente l'occupazione di memoria.

---

### Glossario e Concetti Chiave

- **Codice Prefix-Free**: Codice in cui nessuna parola di codice è il prefisso di un'altra, eliminando le ambiguità durante la decodifica.

- **Elias Gamma/Delta**: Metodi di codifica universale che comprimono interi positivi concatenando un'informazione sulla lunghezza alla rappresentazione binaria del numero.

- **Variable Byte (VB)**: Codifica allineata ai byte che utilizza un bit di controllo per gestire interi di lunghezza variabile, ottimizzando la velocità di calcolo.

- **D-gaps**: Tecnica che consiste nel codificare la differenza tra interi consecutivi in una lista ordinata, riducendo la magnitudo dei valori da processare.

- **Limite Combinatorio**: Il numero minimo teorico di bit necessari per codificare una lista di $n$ elementi in un universo $U$.

---

### Impacchettamento con Simple9 e Simple16

Un'importante strategia di ottimizzazione consiste nell'impacchettare il maggior numero possibile di interi all'interno di una singola "word" di memoria da 32 bit. Il numero esatto di gap o interi che possono trovare spazio in questa word dipende dalla grandezza binaria dei numeri stessi. Per comunicare al decompressore come è stata strutturata la word, viene utilizzato un "selector code" (codice selettore) di 4 bit all'inizio del blocco. Il formato **Simple9** prevede 9 diverse configurazioni per l'organizzazione dei 32 bit, mentre il formato **Simple16** ne mette a disposizione ben 16.

Ad esempio, se il selettore è impostato su `0000`, significa che la word conterrà 28 interi codificati con 1 solo bit ciascuno, senza sprecare alcuno spazio. Al contrario, un selettore `1000` allocherà l'intero spazio rimanente di 28 bit a un singolo intero.

| **Selettore a 4-bit** | **Numero di interi** | **Bit per intero** | **Bit sprecati** |
| --------------------- | -------------------- | ------------------ | ---------------- |
| 0000                  | 28                   | 1                  | 0                |
| 0001                  | 14                   | 2                  | 0                |
| 0010                  | 9                    | 3                  | 1                |
| 0011                  | 7                    | 4                  | 0                |
| 0100                  | 5                    | 5                  | 3                |
| 0101                  | 4                    | 7                  | 0                |
| 0110                  | 3                    | 9                  | 1                |
| 0111                  | 2                    | 14                 | 0                |
| 1000                  | 1                    | 28                 | 0                |
|                       |                      |                    |                  |

### Patched Frame of Reference (PFor)

Quando si analizzano blocchi di gap, la presenza di un singolo valore numerico anomalo può vanificare la compressione. Se in un blocco di numeri piccoli si presenta un valore come 8247, la larghezza binaria di tutti i numeri nel blocco dovrà essere forzata a $\lceil \log_2 8247 \rceil = 14$ bit, sprecando memoria per interi che normalmente ne richiederebbero solo uno. La tecnica **Patched Frame of Reference (PFor)** aggira l'ostacolo definendo un valore di base $b$ e un parametro $k > 0$ calcolati in modo tale che la stragrande maggioranza degli interi (ad esempio il 90%) ricada comodamente nel range $[b, b+2^k-1)$. Ogni intero appartenente a questo intervallo viene codificato semplicemente sottraendo la base, registrando il delta $x-b$ in esattamente $k$ bit, un metodo noto come PForDelta. Tutti i valori eccezionali che superano la soglia ($x \ge b+2^k-1$) ricevono una codeword speciale di eccezione pari a $2^k-1$ e vengono spostati e codificati all'interno di una lista separata.

### Codifica Binaria Interpolativa (Binary Interpolative Coding)

Questa tecnica spicca per la sua abilità di dedurre dinamicamente i bit necessari per il prossimo elemento basandosi sulla conoscenza degli elementi già codificati. Immaginando di dover comprimere una porzione di sequenza delimitata in basso da $l \le L[1]$ e in alto da $h \ge L[n]$, l'algoritmo seleziona l'elemento esattamente a metà, ovvero $L[m]$ con $m = \lceil n/2 \rceil$. Sapendo con certezza che $l \le L[m] \le h$, è possibile codificare lo scarto $L[m]-l$ impiegando esclusivamente $\lceil \log_2(h-l) \rceil$ bit.

Il processo viene quindi reiterato ricorsivamente per le due metà rimanenti della sequenza, aggiornando intelligentemente i limiti per restringere il campo: la porzione inferiore $L[1..m-1]$ riceverà i nuovi limiti $(l, L[m]-1)$, mentre quella superiore $L[m+1..n]$ userà $(L[m]+1, h)$. L'aspetto fondamentale che rende l'Interpolative Coding estremamente potente per sequenze ravvicinate ("clustered") è che, qualora la lunghezza di un intervallo di analisi $r$ risulti uguale alla differenza $h-l$, il sistema rileva un blocco ininterrotto di interi consecutivi, permettendo di omettere del tutto la scrittura di bit per la loro codifica.

[INSERIRE IMMAGINE: Grafo ad albero rovesciato che mostra l'esempio di Binary Interpolative Coding sulla sequenza L=[3,4,7,13,14,15,21,25,36,38,54,62]. Mostra la divisione ricorsiva basata su n, m, l, h e i casi in fondo in cui non è necessaria alcuna codifica]

### Interrogazioni Rapide: Rank e Select

Per esplorare istantaneamente le sequenze di dati compressi si ricorre a operatori specializzati chiamati **Rank** e **Select**, eseguiti direttamente su vettori di bit $B$. La funzione $Rank_0(j)$ conta matematicamente il numero di zeri che compaiono dal primo bit fino alla posizione $j$, così come $Rank_1(j)$ restituisce il conteggio degli uni in quello stesso intervallo $B[0,j]$. Specularmente, le interrogazioni $Select_0(j)$ e $Select_1(j)$ calcolano in modo esatto la coordinata o la posizione in cui risiede il j-esimo zero o il j-esimo uno all'interno del vettore.

Da un punto di vista dell'ottimizzazione, il pregio di queste interrogazioni è il tempo di esecuzione istantaneo e costante pari a $O(1)$. A livello di archiviazione, queste strutture dati domandano un sovrapprezzo spaziale contenuto a $n + o(n)$ bit per il Rank e a $cn + o(n)$ bit per il Select. Per mantenere tempi di query così reattivi, il sistema fa affidamento su array ausiliari (indicati come $B'$ e $B''$) popolati con un quantitativo di metadati pari a $O(n/\log n)$. In questi indici, i valori pre-calcolati sono fissati a scatti di $\log n$ bit, tracciando delle scorciatoie matematiche che scongiurano la scansione lineare e accelerano le operazioni.

[INSERIRE IMMAGINE: Schema concettuale delle interrogazioni Rank e Select. Alla base troviamo l'array B in bit, sovrastato da blocchi di memoria B' e B'' che immagazzinano contatori di scarto intervallati ogni log n salti, evidenziando il tempo O(1)]

### La Rappresentazione Elias-Fano

La struttura **Elias-Fano** rappresenta un'evoluzione architetturale volta a gestire una sequenza $S$ composta da $n$ interi positivi, crescenti e strettamente limitati fino a un tetto massimo definito $U$ (dove, come specificato, $U = (\max n \in S) + 1$). Questa rappresentazione vanta performance teoriche di massimo livello: lo spazio totale occupato in bit si assesta sull'equazione $n \log(U/n) + 2n$. Anche i tempi di ispezione brillano: l'esecuzione della query strutturale $Access(i)$ costa un tempo elementare $O(1)$, mentre la ricerca avanzata $NextGEQ(x)$ impiega appena un tempo algoritmico $O(\log(U/n))$.

La creazione dell'indice prevede di scrivere in verticale (dal basso verso l'alto) la rappresentazione binaria totale. L'innovazione si fonda nel partizionare i $\log U$ bit che descrivono i dati in due tronconi netti. La parte più bassa è formata dai bit espliciti di lunghezza $\log(U/n)$, mentre la porzione alta conta esattamente $\log n$ bit. I bit superiori si interfacciano con un sistema di raggruppamento (bucket), fissando il numero di bucket massimi possibili alla formula $2^{\log n} = n$.

Per rendere fruibile l'indice viene prodotto l'array di navigazione strutturale $H$. Questo traccia quanti elementi gravitano in un bucket scrivendo semplicemente le loro cardinalità mediante il codice unario. Praticamente l'array pone un bit `1` come contatore per ogni singolo intero che vive nella sequenza originaria $S$, e al massimo un bit `0` per tracciare la conclusione di ciascun bucket in memoria. Da questo intreccio deriva l'incremento di soli $2n$ bit. Nel momento in cui giungono richieste $NextGEQ$, il sistema è capace di navigare a colpo sicuro nei raggruppamenti decomprimendo non tutto il database, ma unicamente la ristretta manciata di interi confinati in quello specifico bucket.

[INSERIRE IMMAGINE: Costruzione della struttura Elias-Fano con l'esempio S=[2, 3, 5, 7, 11, 13, 14, 24]. Si distinguono due aree principali sovrapposte che tagliano verticalmente il codice binario in segmenti log(n) per i bucket in verde, log(U/n) trasparente, e la serie finale dell'array unario H in basso]

---

### Glossario / Concetti Chiave

- **Simple9/Simple16**: Metodologie dirette di impacchettamento che stivano un numero variabile di interi all'interno di una singola word da 32 bit, guidate da un selettore iniziale e basate sulla magnitudo dei dati.

- **Patched Frame of Reference (PFor)**: Algoritmo ideato per tollerare valori numerici eccezionali ricalcolando gli elementi piccoli come delta ($x-b$) rispetto a una base e deviando gli elementi troppo grandi in una memoria separata.

- **Binary Interpolative Coding**: Codifica che applica un approccio a divisione ricorsiva ($l$ e $h$), determinando i bit necessari dal contesto circostante. Eccelle nello smaltire le sequenze continue azzerando l'ingombro.

- **Rank e Select**: Operazioni fondamentali su vettori di bit eseguibili in tempo $O(1)$. Permettono di quantificare (Rank) o posizionare (Select) rapidamente porzioni pre-calcolate all'interno del flusso dei dati compressi.

- **Elias-Fano Representation**: Modello di memorizzazione che splitta i bit di elementi crescenti appoggiandosi ai bucket. Permette operazioni complesse come il $NextGEQ$ sfruttando interrogazioni su un array di controllo formattato in codice unario.

---

# Slide 6: Learning to Rank

Questo capitolo introduce il concetto di **Learning to Rank** nell'ambito dell'Information Retrieval, esplorando l'architettura generale di un motore di ricerca, le diverse tipologie di funzioni di ranking e introducendo i fondamenti del Machine Learning necessari per apprendere tali funzioni dai dati.

### Architettura Generale e Flusso di Dati

Il processo di Learning to Rank si inserisce all'interno di un'architettura di ricerca complessa, strutturata su due macro-fasi parallele: una fase **Offline** e una fase **Online**.

Nella fase offline, si parte da una **Document Collection** che viene sottoposta a una procedura di **Indexing** per generare l'**Inverted Index**, fondamentale per il recupero rapido delle informazioni. Parallelamente, la stessa collezione di documenti viene analizzata da un **Feature Processor**, il cui scopo è estrarre caratteristiche utili e salvarle in un **Document Features Repository**. Sempre offline, un set di **Training Data** viene utilizzato in un processo di **Training** per generare e istruire il vero e proprio **Learning-to-rank Model**.

Nella fase online, ovvero quando il sistema è in esecuzione per l'utente, il flusso inizia con l'inserimento di una **Query**. Questa viene inizialmente trasformata in una **Expanded Query** per migliorarne l'efficacia. Il passo successivo è il **Query Processing**, che interroga direttamente l'**Inverted Index** precedentemente costruito. Una volta individuati i documenti candidati, il sistema esegue una **Feature Lookup and Computation**, attingendo le caratteristiche necessarie dal **Document Features Repository**. Infine, queste feature vengono passate alla **Learned Ranking Function** (guidata dal modello addestrato) per calcolare il punteggio finale e produrre la pagina dei risultati mostrata a schermo.

[INSERIRE IMMAGINE: Diagramma di flusso dell'architettura di un motore di ricerca. Mostra le componenti "Online" (Query -> Expanded Query -> Query Processing -> Feature Lookup -> Learned Ranking Function) e "Offline" (Inverted Index, Document Features Repository, Learning-to-rank Model addestrato sui Training Data).]

### Decomposizione delle Funzioni di Ranking e Modelli Bag-of-Words

Per comprendere a fondo come operano le **General ranking functions**, possiamo utilizzare una decomposizione basata sulle rappresentazioni (una schematizzazione adattata dal lavoro di Guo et al., pubblicato nel 2020 su *Information Processing & Management*). In questo modello teorico, abbiamo una **Query q** che viene elaborata in una **Query representation** $\phi$, e un **Document d** che genera una **Document representation** $\psi$. Inoltre, l'interazione diretta tra i due produce una **Query-document representation $\eta$**. Queste tre rappresentazioni alimentano insieme una **Aggregation function f**, la quale calcola e restituisce il **Relevance score $s(q,d)$**.

Questo schema si semplifica drasticamente quando si analizzano le **BOW ranking functions**, ovvero le classiche funzioni basate sui modelli Bag-of-Words. In queste architetture, query e documenti sono rappresentati unicamente come vettori sparsi (**sparse BOW vectors**), trattati essenzialmente come multi-insiemi di parole. Di conseguenza, **non ci sono query-document features**; la componente relativa alla **Query-document representation $\eta$** viene completamente scartata dal processo di calcolo. Esempi classici di questa specifica funzione di aggregazione $f$ sono il **cosine** similarity e l'algoritmo **BM25**. Queste rappresentazioni sparse sono fisicamente archiviate negli inverted index, che costituiscono la vera e propria spina dorsale (**backbone**) dei motori di ricerca web commerciali (**commercial Web search engine**).

[INSERIRE IMMAGINE: Diagramma della decomposizione basata sulle rappresentazioni per i modelli BOW. La casella centrale "Query-document representation" è sbarrata da una grande X rossa, a indicare l'assenza di feature combinate.]

### Estensione del Framework e Integrazione di Nuovi Segnali

Constatato che finora query e documenti sono stati considerati come meri multi-insiemi di parole, è naturale chiedersi se esistano altre istanze applicabili al nostro framework generale per superare questo limite. La risposta è affermativa: esiste infatti una grande quantità di altri potenziali segnali da poter utilizzare.

Quali potrebbero essere queste idee aggiuntive?. Lato documento, si può sfruttare il fatto che esso può avere dei campi strutturati, può essere suddiviso in zone o, ancora, può essere arricchito con dati testuali esterni, come ad esempio il testo dei link in ingresso (**anchors**). Si rivelano preziose anche informazioni aggiuntive di natura strutturale e comportamentale, come i link in entrata (**In-Links**), i link in uscita (**Out-Links**), il valore di **PageRank**, il numero di **clicks** ricevuti o i **social links**. Lato query, invece, si possono integrare segnali quali il numero di termini inseriti (**# terms**), la popolarità della ricerca nei log del motore (**popularity in query logs**) o le informazioni estratte dal profilo dell'utente (**user profile info**). Reintroducendo tutti questi dati nel modello, il framework torna a sfruttare a pieno tutte le sue componenti, reintegrando attivamente la **Query-document representation $\eta$** nel calcolo della funzione di aggregazione.

[INSERIRE IMMAGINE: Diagramma del General framework completo. Nessuna componente è sbarrata; Query representation, Document representation e Query-document representation contribuiscono congiuntamente all'Aggregation function f.]

### L'Apprendimento della Funzione tramite Machine Learning

L'inclusione di tutti questi segnali porta a un interrogativo critico: **possiamo imparare f?**. Imparare questa complessa funzione di aggregazione significa affidarsi alle tecniche di **Machine Learning**.

Secondo la definizione di Wikipedia, il Machine learning è un campo dell'informatica che utilizza tecniche statistiche per consentire ai sistemi informatici di apprendere. Questo si traduce nella capacità di migliorare progressivamente le prestazioni su un compito specifico attingendo dai dati, senza dover programmare il sistema in modo esplicito. Questo approccio metodologico implica necessariamente due precondizioni fondamentali: la disponibilità e l'esistenza di dati (**Existence of data**), e la definizione formale di un problema di ottimizzazione (**Optimization problem**), che richiede di stabilire un task specifico e una metrica di misurazione (**task? measure?**).

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

[INSERIRE IMMAGINE: Esempio visivo di classificazione supervisionata con le foto di un gatto e di un cucciolo di cane associati alle rispettive etichette testuali "cat" e "dog"].

All'interno di un dataset, ogni singolo elemento da analizzare viene definito **osservazione** o **istanza** (**observation** o **instance**). Queste istanze sono descritte e caratterizzate da un insieme di **feature**, ovvero proprietà specifiche misurabili. Nell'apprendimento supervisionato, ogni istanza è accompagnata da un'etichetta, definita **label**, che rappresenta la "soluzione" o il valore da prevedere. Sfruttando lo spazio di queste feature e la supervisione delle label, i problemi di Machine Learning si dividono in due rami principali: i task di **Regression** (regressione), dove l'obiettivo è prevedere una specifica quantità numerica reale, e i task di **Classification** (classificazione), in cui si assegnano etichette discrete a istanze di dati la cui categoria non è conosciuta in anticipo.

Tutte queste attività di apprendimento (sia supervisionate che non) si strutturano in due fasi temporali e logiche ben distinte. La prima è la **training phase** (fase di addestramento), che avviene tipicamente offline. In questo momento il modello viene costruito, o per usare il gergo tecnico, si esegue il "fit a model on a given set of data". La seconda è la **testing phase** (fase di test), generalmente eseguita online direttamente per l'utente , in cui il modello precedentemente addestrato viene impiegato per effettuare le previsioni vere e proprie, ovvero "to predict class/label of a given set of data".

### Il Ciclo di Apprendimento: Un Parallelo con i Puzzle

Per comprendere intuitivamente questo processo di calibrazione degli algoritmi, possiamo fare un parallelismo con i classici giochi di enigmistica per bambini.

[RIFERIMENTO VISIVO DEL PROFESSORE: La copertina di un libro di puzzle e giochi logici per bambini, affiancata da piccoli labirinti e bilance, usata come metafora per spiegare l'allenamento della mente rispetto a quello di una rete neurale].

Per ogni puzzle, abbiamo a disposizione il problema stesso e, in fondo al libro, la sua soluzione corretta. Di fronte alla sfida, il nostro cervello produce inizialmente un tentativo di soluzione e, in un secondo momento, lo confrontiamo con quella esatta. Osservando le differenze tra la nostra risposta e quella reale, riusciamo a sintonizzare il nostro pensiero e ad apprendere la logica corretta. Ripetendo questa operazione ("ad libitum") per molti giochi, diventiamo sempre più bravi.

Traducendo questa semplice metafora nel framework formale del Machine Learning, un'istanza di addestramento derivante dall'ambiente (**Environment**) viene presentata al sistema di apprendimento (**Learning system**). L'algoritmo produce quindi in uscita una previsione sul momento, ovvero un **Actual output**. Contemporaneamente, una sorta di "Teacher" (Insegnante) fornisce l'output desiderato. La differenza matematica tra la previsione appena calcolata e l'etichetta reale dell'istanza viene elaborata da un sommatore (**Adder**) e prende il nome di **loss** o **error** (errore). L'importanza vitale della loss sta nel fatto che viene "restituita" all'algoritmo e utilizzata per aggiornare i parametri interni del suo modello. Questo meccanismo ciclico viene ripetuto per tutte le istanze che compongono il dataset di addestramento. Quando l'intero dataset viene processato, diciamo di aver completato un ciclo; e poiché il processo intero viene ripetuto svariate volte per ottimizzare l'algoritmo, misuriamo questo scorrere del tempo in **learning iterations** o **training epochs** (epoche di addestramento).

[INSERIRE IMMAGINE: Diagramma a blocchi del framework generale di apprendimento supervisionato. Mostra il flusso partendo dall'Environment verso il Learning system (che produce l'Actual output) e verso il Teacher (che fornisce il Desired output). Entrambi gli output convergono in un nodo Adder circolare, che calcola l'Error (Loss) e lo ritrasmette retroattivamente al Learning system per guidarne l'aggiornamento].

### Il Task del Learning to Rank (LtR)

Cosa succede quando applichiamo queste dinamiche al recupero delle informazioni? L'obiettivo del **Learning to Rank (LtR)** è produrre e ottimizzare le liste ordinate dei risultati. Per fare ciò, la fase di addestramento necessita di un training set di grandi dimensioni, composto da interrogazioni degli utenti affiancate all'ordinamento ideale dei documenti rispetto ad esse.

[RIFERIMENTO VISIVO DEL PROFESSORE: Un esempio di query "qa" seguita da una sequenza di coppie documento-rilevanza, come d4 valutato ya0, e d2 valutato ya1, per mostrare come i dati in ingresso indichino al sistema quali documenti posizionare in cima].

Invece di processare singole parole, il sistema gestisce l'interazione tra la query "q" e un documento "d" rappresentandola come uno specifico **Feature vector** (ad esempio denotato come $<q,d> = <f0, f1...>$), che funge da input per il modello. Questo modello matematico funziona operativamente come una scatola nera (**Black Box**) che riceve query e vari documenti, restituendo infine una singola **ranked list**. Per guidare questa "scatola nera" durante il training, si usano etichette numeriche "y" che indicano il grado di pertinenza reale; un esempio tipico sfrutta una scala a cinque valori, che va da 0 (documento del tutto irrilevante) fino a 4 (documento perfettamente rilevante). È cruciale evidenziare una dinamica controintuitiva ma fondamentale del Learning to Rank: benché i modelli di intelligenza artificiale addestrati agiscano tecnicamente come regressori che assegnano un punteggio numerico a ogni singolo candidato, lo scopo ultimo e misurabile del sistema resta esclusivamente quello di indovinare l'ordinamento generale della lista, non di replicare precisamente quelle specifiche etichette numeriche.

### Le Feature nei Sistemi di Ranking Moderni

Il successo di un sistema basato su feature vector dipende da quanti e quali segnali si riescono a raccogliere. Già molto tempo fa (in un articolo del New York Times datato 3 giugno 2008), l'ingegnere Amit Singhal rivelò che Google stava utilizzando oltre 200 diverse feature all'interno del suo motore. Questa grande famiglia di parametri comprende indicatori fisici e strutturali, come il numero di link in uscita presenti sulla pagina, il numero totale di immagini, la lunghezza del documento testuale o la lunghezza dell'URL. Si valutano anche dettagli microscopici ma utili per l'anti-spam, come la presenza del carattere tilde ('~') all'interno dell'indirizzo Web , oltre a indicatori di freschezza del contenuto come la data dell'ultima modifica (Page edit recency) e misurazioni globali di autorità come il PageRank. Ad alimentare questi modelli si aggiungono fattori di match testuale (come il punteggio BM25, la comparsa in grassetto o a colori della parola cercata e la sua frequenza logaritmica nei link diretti alla pagina) e importantissimi fattori comportamentali, quali il conteggio dei click generali, i click in corrispondenza della query specifica e il tempo speso dall'utente a leggere il risultato (url dwell time).

Tutte queste innumerevoli feature vengono categorizzate concettualmente dal framework in tre grandi macrogruppi. Il primo è formato dalle **query-only features**: si tratta di caratteristiche che assumono lo stesso valore per ogni documento analizzato in quella sessione, essendo legate esclusivamente alla tipologia o alla lunghezza della query dell'utente. Il secondo raggruppamento è quello delle **query-independent features**, che raggruppa le caratteristiche intrinseche al documento il cui valore rimane costante a prescindere da chi o cosa stia cercando in quel momento. Ne sono classici esempi il PageRank della pagina e la lunghezza del suo URL. Infine, le più dinamiche sono le **query-dependent features**: sono quelle metriche generate in tempo reale dall'interazione tra la richiesta e il documento, come il punteggio BM25, la frequenza della query in grassetto e le statistiche dei click passati per quella specifica correlazione query-URL.

[INSERIRE IMMAGINE: Ripresa del diagramma ad albero relativo alla scomposizione di una funzione di ranking, ora arricchito con le definizioni dei tre tipi di feature. Evidenzia che la Query representation riceve le query-only features, la Document representation assimila le query-independent features e la Query-document representation centrale elabora le query-dependent features].

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

[INSERIRE IMMAGINE: Diagramma della "Learning to Rank pipeline". Mostra il First step con il Base Ranker e il Document Index che estraggono "N docs", seguiti dal Second step con il Top Ranker, le Features e l'algoritmo LtR che raffinano la lista in "K docs" per la pagina dei risultati]

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

Per discriminare i documenti, possiamo definire una **funzione di punteggio lineare** espressa dall'equazione $Score(d,q) = a\alpha + b\omega$. Trattandosi di un problema simile alla text classification, il classificatore lineare dovrà determinare in autonomia i pesi dei parametri $a$ e $b$, oltre a una soglia limite **$\Theta$**. Il sistema deciderà quindi di etichettare la coppia come rilevante se il risultato dell'equazione supera tale soglia ($Score(d,q) > \Theta$) o come irrilevante in caso contrario ($Score(d,q) \le \Theta$).

[INSERIRE IMMAGINE: Grafico cartesiano che mostra una "Decision surface" definita dalla retta dell'equazione lineare. L'asse X rappresenta la "Term proximity", l'asse Y il "cosine score". I punti "R" (rilevanti) e "N" (non rilevanti) sono separati linearmente da questa soglia decisionale]

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

[INSERIRE IMMAGINE: Grafico cartesiano della "Pivoted length normalization" per il calcolo del BM25. Sull'asse X c'è la lunghezza del documento $|d|$, sull'asse Y il valore del normalizzatore. Mostra tre rette per b=0, b>0 e b>>0, illustrando come i valori inferiori alla lunghezza media (avdl) ricevano un "Reward", e quelli superiori una "Penalization"]

Per mitigare ulteriormente l'impatto esplosivo di frequenze molto elevate per un singolo termine, il risultato $F_t$ non viene utilizzato puro, ma processato da una **funzione di saturazione** $\tau(F_t) = \frac{F_t}{k + F_t}$. Questa dinamica introduce una non-linearità fondamentale nel contributo della frequenza. Qualora non si volesse passare per un processo di ottimizzazione personalizzato, il BM25 prevede due iperparametri di default solitamente molto stabili: $k$ impostato tra 1.2 e 2, e $b = 0.75$.

[INSERIRE IMMAGINE: Grafico delle curve della funzione di saturazione per il BM25. Le linee curve mostrano come il valore y aumenti rapidamente per bassi valori della frequenza x, ma tenda ad appiattirsi (saturare) asintoticamente man mano che x diventa molto grande, dimostrando l'effetto dell'iperparametro k]

### L'Estensione ai Campi Strutturati: Il Modello BM25F

I documenti complessi moderni (come articoli accademici o intere pagine web) raramente sono blocchi testuali monolitici; si presentano come artefatti ricchi di **campi strutturati** o multi-field (es. titolo, abstract, riassunto, autori per un paper; titolo, url, corpo della pagina, ancore per un sito web).

Per gestire questo grado di strutturazione, la formula originale è stata estesa nel modello **BM25F**, rappresentato come:

$$BM25F(d,q) = \sum_{t} IDF_t \tau(F_t)$$

$$F_t = \sum_{s} \frac{w_s \cdot f_{t,s}}{1 - b_s + b_s \cdot l_s / L_s}$$

In questa variante, le frequenze vengono calcolate a livello del singolo segmento semantico. La variabile $f_{t,s}$ rappresenta la frequenza del termine nel campo specifico $s$, valutata in relazione alla lunghezza di tale campo $l_s$ e alla media globale delle lunghezze per la stessa tipologia di campo $L_s$. Rispetto al modello di base, viene introdotto un nuovo moltiplicatore cruciale: **$w_s$**, che assegna un peso variabile e definisce l'importanza relativa del campo $s$ rispetto agli altri. Questa estensione genera un'esplosione dei parametri da configurare. Mentre il BM25 classico possedeva solo 2 parametri liberi ($b$ e $k$), il BM25F richiede di gestire ben **$2S + 1$** parametri (dove $S$ indica il numero totale dei campi analizzati), ossia i pesi $w_s$, le penalizzazioni $b_s$ per ogni campo, più la costante di saturazione globale $k$. Questa mole di variabili rende impossibile una calibrazione manuale e costringe ad affidarsi proprio alle metodologie del **Learning to Rank** per individuare la combinazione ottimale.

### Sfide nell'Ottimizzazione Listwise e Tecniche Adottate

Considerando l'applicazione di algoritmi Machine Learning al tuning del BM25F, per prima cosa definiamo un dataset dove ogni query è associata a candidati annotati manualmente con etichette di rilevanza progressiva (da 0 a 4 stelle). Lo spazio delle ipotesi sarà composto da tutte le infinite e possibili variazioni delle funzioni BM25F. L'obiettivo valutativo è massimizzare una metrica listwise complessa, ad esempio la **Normalized Discounted Cumulative Gain (NDCG@10)**, la cui formula del gain cumulativo è:

$$DCG_p = \sum_{i=1}^{p} \frac{2^{rel_i} - 1}{log(1+i)}$$

Questa equazione scala l'importanza del documento in modo esponenziale rispetto alla sua etichetta di partenza: un documento con relevance 0 porta un incremento di 0 punti, mentre uno con punteggio massimo (4) inietta ben 17 punti di scarto ($2^4 - 1$).

Posti l'obiettivo di apprendere un modello $h$ (ovvero un modello BM25F governato dal set di parametri $\Theta$) per ordinare un set di documenti ($D = \{d_1, d_2, ...\}$), incontriamo un grave ostacolo teorico per gli approcci Listwise puri. metriche come l'NDCG dipendono intrinsecamente dalle posizioni finali occupate dai risultati e non dai semplici punteggi numerici calcolati. In altre parole, l'operazione di ordinamento globale (**sort $\{h(d_1), h(d_2), ...\}$**) gioca un ruolo centrale. Purtroppo, l'operazione di ordinamento (sort) non costituisce una funzione continua né derivabile. Non potendo calcolare un gradiente dell'ordinamento, l'ottimizzazione tramite Gradient Descent diviene strutturalmente inapplicabile in modo diretto (un problema ampiamente analizzato da M. Taylor e H. Zaragoza nell'articolo *Optimisation Methods for Ranking Functions with Multiple Parameters*, CIKM-06).

Per bypassare questa difficoltà, l'Information Retrieval adotta vie traverse:

- Si minimizza una **loss pairwise proxy** (come il costo generato dall'algoritmo RankNet), pur sapendo che ottimizzare gli incroci documentali a coppie non equivale alla perfezione listwise.

- Si fa ricorso a soluzioni molto più efficaci, come il **Lambda-MART**, un algoritmo strutturato attorno a una funzione di costo modificata che riesce ad approssimare pseudo-gradienti utili per ottimizzare le metriche NDGC in via indiretta. Il MART, conosciuto anche come **Gradient Boosted Regression Tree (GBRT)**, utilizza insiemi di alberi di regressione, e librerie specializzate come **LightGBM** permettono di sfruttare oggi agilmente questa tecnologia.

[INSERIRE IMMAGINE: Grafico che illustra l'andamento discorde tra l'ottimizzazione Pairwise e il guadagno metrico reale durante le epoche di training. Viene confrontata la curva blu, che mostra la costante diminuzione della loss Pairwise (fino a convergere), e la curva rossa estremamente instabile dell'NDCG@5, che non subisce reali benefici dopo una prima fase iniziale, a dimostrazione dello scollamento tra le due misure].

Questa problematica evidenzia infatti il più grande difetto dell'approccio Pairwise puro applicato all'Information Retrieval. Questo metodo si impegna a minimizzare il volume totale delle coppie classificate scorrettamente nel sistema. Tuttavia, nel mondo reale e per ottimizzare correttamente l'NDCG, l'ordinamento accurato dei primi risultati mostrati (**top result pairs**) assume un'importanza enormemente superiore rispetto alla perfezione dell'ordine nelle posizioni periferiche o basse dell'elenco. Per tale motivo, quantificare genericamente le violazioni delle coppie di documenti senza pesarle posizionalmente non produrrà mai un indicatore affidabile per massimizzare le valutazioni NDCG destinate agli utenti.

### Glossario e Concetti Chiave

- **Term Proximity**: La metrica che valuta la vicinanza strutturale tra i vari termini della ricerca all'interno del corpo testuale della pagina, definendone la densità contestuale.

- **BM25 / BM25F**: Funzioni matematiche probabilistiche all'avanguardia basate sull'indipendenza dei termini; la versione F è specializzata nel gestire documenti segmentati in molteplici campi (title, abstract, test, ecc.), richiedendo un elevato numero di parametri da sintonizzare.

- **Pivoted Length Normalization**: Una specifica tecnica all'interno degli algoritmi di penalizzazione che sfavorisce proporzionalmente i documenti testuali eccessivamente lunghi per evitare che i loro ampi vocabolari "acchiappino" ingiustamente un punteggio elevato.

- **DCG / NDCG**: Sigla di (Normalized) Discounted Cumulative Gain, è una complessa metrica di valutazione Listwise che sfrutta potenze matematiche per premiare smisuratamente un documento eccellente posto nelle primissime posizioni della classifica, svalutando proporzionalmente i documenti utili se relegati in fondo alla pagina.

- **Lambda-MART / GBRT**: Sofisticati algoritmi di Machine Learning basati su alberi decisionali che riescono ad approssimare la massimizzazione dell'NDCG aggirando matematicamente il problema della non-derivabilità della funzione di ordinamento (sort).

---

# Slide 7: Il Paradigma del Learning to Rank

Questo capitolo illustra l'applicazione delle tecniche di Machine Learning all'Information Retrieval (IR), concentrandosi in particolare sull'uso di feature elaborate manualmente e sulle sfide computazionali e matematiche insite nel processo di ordinamento dei documenti.

### Machine Learning per l'IR e Computazione Online

L'architettura tipica di un sistema di Learning to Rank che opera in una fase di online computation prevede l'estrazione di un **vettore di feature** elaborato manualmente. In questo processo, un documento $d$ composto da $m$ token e una query $q$ formata da $n$ token vengono passati a un estrattore di feature (Feature Extractor). L'input per l'addestramento è dunque costituito da questo vettore di feature, che modella la rilevanza della coppia query-documento, unito alle rispettive etichette (labels). Queste informazioni vengono poi fornite a un **Modello di Ranking**, il quale si occupa di calcolare un punteggio finale di rilevanza, indicato tipicamente come $S_{q,d}$. L'approccio che ad oggi rappresenta lo stato dell'arte si basa su una foresta composta da migliaia di alberi di regressione. Sebbene questa soluzione garantisca una qualità altissima dei risultati, il suo utilizzo in produzione risulta computazionalmente molto dispendioso.

[INSERIRE IMMAGINE: Diagramma di flusso del processo di online computation, illustrante come un documento e una query entrino in un feature extractor per produrre un feature vector, il quale passa al ranking model per restituire uno score di rilevanza].

### La Complessità del Ranking e la Proxy Loss

Il task di addestrare un modello di ranking si rivela un'operazione estremamente complessa. L'obiettivo principale è imparare l'ordinamento (il ranking) stesso e non la singola etichetta del documento, lavorando su dataset enormi caratterizzati da centinaia di feature differenti. Il problema matematico fondamentale è che le tradizionali misure di qualità del ranking basate sul grado di posizionamento, come NDCG, ERR e MAP, dipendono intrinsecamente dall'ordine in cui i documenti sono stati ordinati. Di conseguenza, queste funzioni non presentano derivate agevoli e impediscono l'applicazione diretta di algoritmi classici come la discesa del gradiente (gradient descent). Osservando i documenti (es. $d_0$, $d_1$, $d_2$, $d_3$), il gradiente di queste metriche rispetto ai parametri del modello risulta essere o pari a 0, se le variazioni dei pesi non hanno alterato l'ordinamento, oppure indefinito a causa delle repentine discontinuità della funzione. La soluzione adottata nell'IR moderno è l'introduzione di una **Proxy Loss function**. Questa funzione vicaria deve essere differenziabile e, allo stesso tempo, mostrare un comportamento il più possibile fedele alla funzione di costo originale, permettendo così al modello di apprendere con successo i parametri ottimali.

[INSERIRE IMMAGINE: Due grafici a confronto. Il primo mostra l'andamento a gradini della metrica NDCG@k in relazione al document score, evidenziando le discontinuità; il secondo mostra la Proxy Quality Function, caratterizzata da una curva differenziabile e smussata rispetto agli stessi punteggi documentali].

### Algoritmi Point-Wise e Alberi di Decisione

Il primo gruppo di algoritmi analizzabili è quello **Point-Wise**. In questa famiglia di modelli, ogni documento viene valutato in modo strettamente indipendente dagli altri. Durante la fase di addestramento, per una singola istanza formata dal documento $d_i$ e dalla sua etichetta $y_i$, non viene utilizzata alcuna informazione relativa agli altri candidati associati alla medesima query. L'algoritmo ottimizza pertanto una funzione di costo differente rispetto alle metriche list-wise e lo fa sfruttando vari approcci, tra cui la Regressione, la Classificazione Multi-Classe o la Regressione Ordinale.

All'interno degli algoritmi basati sulla regressione, assumono grande importanza i **Gradient Boosting Regression Trees (GBRT)**. In questo specifico modello di addestramento, la Loss Function impiegata è orientata alla minimizzazione della Somma degli Errori Quadratici (SSE). Prima di approfondire i GBRT, è essenziale comprendere la struttura di un Albero di Decisione e, di conseguenza, di un Albero di Regressione. In un tipico albero di decisione, usato ad esempio per la classificazione animale, lo spazio viene diviso tramite domande binarie (l'animale è più grande o più piccolo di 1 metro?, ha le corna?, ha due gambe?, le corna sono più lunghe di 10 cm?, indossa un collare?, ha le ali?, ha la coda?) fino a giungere a una classificazione precisa. Analogamente, un albero di regressione esegue un partizionamento dello spazio dei predittori. Basandosi su variabili, ad esempio $x1$ e $x2$, applica soglie di split sequenziali (come $x2 < 3.1$ o $x1 \geq 6.6$) per instradare i campioni verso le foglie. A differenza della classificazione, ogni nodo foglia restituisce un valore continuo $y$ (come $y=0.75$, $y=2.2$, fino a $y=6.3$), che rappresenta la predizione del punteggio.

[INSERIRE IMMAGINE: Esempio di un albero di regressione corredato dal grafico cartesiano (assi x1 e x2) che mostra il corrispondente partizionamento dello spazio in regioni rettangolari associate ai vari valori predittivi y].

### Gradient Boosting Regression Trees (GBRT)

I GBRT si fondano sul concetto di foreste di alberi, includendo anche algoritmi come Lambda MART, Random Forest e Oblivious Trees. Queste architetture costituiscono un "ensemble" di *weak learners*, dove ciascun albero contribuisce fornendo un punteggio parziale. Al momento dello scoring vero e proprio, un vantaggio cruciale è che tutti gli alberi ($T_1, T_2, \dots, T_n$) possono processare la coppia (q, d) ed essere valutati in modo indipendente, producendo dei punteggi parziali ($S_1, S_2, \dots, S_n$). Il punteggio documentale finale viene poi calcolato aggregando tali contributi parziali mediante la formula: $s(d) = \sum_{i=1}^n w_i s_i$.

L'addestramento dei GBRT avviene tramite un algoritmo strettamente iterativo in cui il modello finale è espresso come $F(d) = \sum_i f_i(d)$. Ciascun termine $f_i$ rappresenta uno dei *weak learners* ed è concepito matematicamente come un passo nella direzione ottimale di minimizzazione dell'errore, comportandosi come uno step di massima pendenza (steepest descent) calcolato tramite line-search. In formule, questo passo si traduce nella discesa lungo il gradiente negativo: $f_i(d) = -\rho_i g_i(d)$. Data una funzione di perdita $L = SSE/2$ , il gradiente $-g_i(d)$ viene formalizzato tramite la derivata $-[\frac{\partial L(y,f(d))}{\partial f(d)}]_{f=\sum_{j<i}f_j}$. Calcolando esplicitamente la derivata, $-\frac{\partial[\frac{1}{2}SSE(y,f(d))]}{\partial f(d)} = -\frac{\partial[\frac{1}{2}\sum(y-f(d))^2]}{\partial f(d)}$, si ottiene la pseudo-risposta $y - f(d)$, che rappresenta banalmente il residuo, ovvero l'errore commesso dalle predizioni accumulate finora rispetto all'etichetta reale. L'algoritmo procede approssimando questo gradiente $g_i$ per mezzo di un nuovo albero di regressione $t_i$. Geometricamente, ciò significa che ogni nuovo albero (da $t_1$ a $t_3$) colma iterativamente la distanza tra il punteggio predetto $F(d)$ e l'obiettivo $y$.

[INSERIRE IMMAGINE: Grafico che illustra l'algoritmo iterativo dei GBRT, dove l'errore complessivo $y - F(d)$ viene progressivamente ridotto sommando l'output sequenziale di diversi alberi di regressione $t_1$, $t_2$ e $t_3$].

### Algoritmi Pair-Wise e il Modello RankNet

Passando al secondo approccio principale, negli algoritmi **Pair-wise** l'unità fondamentale di analisi cessa di essere il singolo documento e diventa la coppia. **RankNet** rappresenta una delle soluzioni Pair-wise più affermate e opera stimando la probabilità che, all'interno di un'istanza formativa, un documento $d_i$ sia superiore in rilevanza rispetto a un documento $d_j$. Definita la differenza dei punteggi predetta dal modello per i due documenti come $o_{ij} = F(d_i) - F(d_j)$ , questa probabilità viene calcolata tramite la funzione logistica $P_{ij} = \frac{e^{o_{ij}}}{1 + e^{o_{ij}}}$. Assumendo che $T_{ij}$ indichi la probabilità reale derivata dalle etichette, RankNet sfrutta una funzione di Cross Entropy Loss definita come $C_{ij} = -T_{ij} \log P_{ij} - (1-T_{ij}) \log(1-P_{ij})$. L'algoritmo filtra il dataset analizzando solamente le coppie dove sussiste una chiara preferenza, ovvero dove l'etichetta di $d_i$ è maggiore di quella di $d_j$ ($y_i > y_j$). In questo scenario, la loss si semplifica notevolmente diventando $C_{ij} = \log(1 + e^{-o_{ij}})$. Da questa equazione si evince il comportamento del modello: se i documenti sono correttamente ordinati, con una predizione marcatamente forte ($o_{ij} \to +\infty$), il costo di errore tende naturalmente a zero ($C_{ij} \to 0$). Al contrario, se la rete si sbaglia e inverte l'ordine corretto restituendo una forte discrepanza negativa ($o_{ij} \to -\infty$), il costo esplode verso infinito ($C_{ij} \to +\infty$). Poiché questa loss function si mantiene strettamente differenziabile, viene sfruttata efficacemente per addestrare Reti Neurali Artificiali (ANN) per mezzo del noto algoritmo di back-propagation.

Altri approcci inclusi nella famiglia Pair-wise comprendono algoritmi come Ranking-SVM e RankBoost. I dati sperimentali confermano che RankNet ottiene performance generali migliori rispetto a questi due competitor per quanto riguarda l'accuratezza del ranking. Esiste, tuttavia, un limite architetturale rilevante da tenere in considerazione: l'andamento della funzione di costo in RankNet non risulta essere adeguatamente correlato con i miglioramenti qualitativi reali che si possono misurare tramite NDCG@5 (o altre metriche orientate all'ordine puro) all'aumentare delle epoche di addestramento.

[INSERIRE IMMAGINE: Grafici riassuntivi delle performance. A sinistra, l'istogramma che paragona RankNet, RankSVM e RankBoost mostrando la superiorità del primo (Figure 1). A destra, il grafico a dispersione (Figure 4) che illustra la debole correlazione tra la minimizzazione della Pairwise loss e il valore della metrica NDCG@5 all'aumentare delle epoche].

---

### Glossario e Concetti Chiave

1. **Proxy Loss Function**: Una funzione differenziabile impiegata durante la fase di addestramento per ovviare alla discontinuità intrinseca delle metriche di ordinamento reali (come NDCG).

2. **Gradient Boosting Regression Trees (GBRT)**: Un potente modello *ensemble* che sfrutta una foresta di alberi di regressione, i quali apprendono iterativamente calcolando passi di *steepest descent* lungo la direzione dell'errore (o gradiente negativo).

3. **Point-Wise vs. Pair-Wise**: Due filosofie di addestramento distinte. L'approccio point-wise ottimizza uno scarto (es. SSE) considerando ciascun documento indipendentemente dagli altri, mentre l'approccio pair-wise (come RankNet) ottimizza le stime di probabilità del corretto posizionamento relativo confrontando le coppie di documenti.

---

### Algoritmi List-wise e LambdaMART

Evoluzionando le filosofie precedenti, gli algoritmi **List-wise**, di cui **LambdaMART** è il massimo esponente, ottimizzano in modo diretto le metriche di valutazione focalizzandosi sull'intera lista dei documenti. Ricordando che i Gradient Boosting Regression Trees (GBRT) hanno la necessità intrinseca di calcolare un gradiente $g_i$ per ciascun documento $d_i$ sottoposto ad analisi , LambdaMART comincia col calcolare la stima di questo gradiente paragonando il documento in esame con un diverso $d_j$, ponendo come condizione essenziale che l'etichetta di rilevanza $y_i$ sia superiore a $y_j$. Questo scarto è descritto dal coefficiente $\lambda_{ij}$, che viene modellato come il prodotto algebrico tra la derivata del costo negativo ricavata da RankNet e il differenziale di qualità (ossia il valore assoluto della variazione $|\Delta NDCG|$) ottenuto nel simulare un ipotetico scambio di posizione tra $d_i$ e $d_j$. La formula completa si esprime quindi come $\lambda_{ij} = \frac{1}{1+e^{o_{ij}}} |\Delta NDCG| [cite_start]= -\lambda_{ji}$. L'analisi di una specifica istanza di training, definita dalla query e dai rispettivi documenti ($d_2, d_3, \dots, d_{|q|}$), rivela un comportamento elegante. Se i due documenti sono già ordinati correttamente in partenza (condizione in cui $o_{ij} \to +\infty$), il gradiente sfuma fisiologicamente verso lo zero ($\lambda_{ij} \to 0$). Viceversa, in caso di cattivo posizionamento ($o_{ij} \to -\infty$), il gradiente si innalza fino a raggiungere il pieno valore dell'errore metrico di ranking $|\Delta NDCG|$. Attraverso questo semplice meccanismo matematico, si obbliga la rete a dare un'importanza molto superiore ai documenti che si posizionano in alto (i top documents), in quanto notoriamente più rilevanti per la soddisfazione dell'utente finale. Al termine di questi step, l'algoritmo GBRT con Lambda Gradients calcola la stima del gradiente finale $g_i$ confrontando $d_i$ con tutti gli altri candidati associati alla query; il calcolo avviene addizionando i pesi delle coppie correttamente disposte e sottraendo invece i valori associati a quelle fallite, applicando la semplice espressione $g_i = \lambda_i = \sum_{y_i>y_j} \lambda_{ij} - \sum_{y_i<y_j} \lambda_{ij}$.

[INSERIRE IMMAGINE: Diagramma che mostra graficamente la foresta sequenziale di alberi di decisione, utilizzata dall'architettura List-wise].

### Risultati Sperimentali ed Evoluzione dei Modelli List-wise

Per valutare la bontà pratica di questi approcci, si osservano tipicamente le prestazioni espresse in termini di metrica NDCG@10 eseguite su vari set di dati (Dataset LtR) aperti al pubblico. Come si evince confrontando la tabella sottostante, il passaggio da approcci classici come RankingSVM a GBRT e LambdaMART ha prodotto valori fortemente eterogenei a seconda del dataset interrogato (quali MSN10K, Y!S1, Y!S2 e Istella-S). Il mercato accademico e industriale non si è comunque fermato, elaborando ulteriori alternative competitive che includono ListNet, ListMLE, Approximate Rank, SVM AP e RankGP.

| **Algorithm**                                                 | **MSN10K** | **Y!S1** | **Y!S2** | **Istella-S** |
| ------------------------------------------------------------- | ---------- | -------- | -------- | ------------- |
| RankingSVM                                                    | 0.7306     | 0.4012   | 0.7238   | N/A           |
| GBRT                                                          | 0.7555     | 0.7620   | 0.4602   | 0.7313        |
| LambdaMART                                                    | 0.4618     | 0.7529   | 0.7531   | 0.7537        |
| *(Metriche espresse in NDCG@10 per i vari dataset indicati)*. |            |          |          |               |

Esaminando storicamente lo sviluppo del Machine Learning in ambito di ricerca testuale, emerge una timeline ricca, nata nei periodi antecedenti al 2005 e proseguita verso approcci concorrenti Pointwise, Pairwise (come RankBoost e RankNet) e Listwise (tra cui AdaRank e ListNet). Gli approcci moderni per massimizzare le misure IR includono oggi varianti come DirectRank, Lambda Mart, BLMart, SSLambdaMART, CoList, LogisticRank e Lambda Loss. Simultaneamente, il Deep Learning sta innovando pesantemente il *matching* tra documento e query, proponendo architetture Conv.DNN, DSSM, meccanismi basati sul Dual-Embedding, nonché tecniche legate alla Weak Supervision e al Neural Click Model. Da citare infine lo spostamento d'attenzione verso sistemi di On-line learning interattivo, regolati da approcci probabilistici come Multi-armed bandits, Dueling bandits e K-armed dueling bandits.

### L'Egemonia Pratica dei GBRT e le Tecniche di Distillazione

L'adozione quasi universale dei GBRT nel panorama industriale deriva da una lunga striscia di successi ottenuti nei più disparati Data Challenge su scala mondiale. L'evidenza principale risale al famoso Yahoo! LtR Challenge, vinto da un team la cui architettura mescolava 12 differenti modelli di ranking; di questi, 8 erano proprio costituiti da modelli Lambda-MART istanziati con grandezze fino a ben 3.000 alberi decisionali l'uno. Questa forte predominanza è stata riconfermata in uno studio statistico del 2015 sulle competizioni Kaggle, indicando che la stragrande maggioranza delle soluzioni trionfanti si appoggiava pesantemente ai GBRT a discapito delle reti deep learning, e che le top-10 squadre arrivate alla KDDCup dello stesso anno utilizzassero tutte algoritmi della famiglia GBRT. Di conseguenza, giganti tecnologici e team indipendenti hanno pubblicato formidabili e ottimizzate piattaforme open-source: XGBoost, la soluzione LightGBM presentata da Microsoft e CatBoost promossa da Yandex. Tali soluzioni sono divenute lo standard in quanto pluggable, potendo essere collegate in modo nativo su sistemi leader come Apache Lucene e Solr.

Eppure, persiste un dubbio architetturale: se le Reti Neurali Profonde (DNN) tendono ad essere intrinsecamente più veloci nell'eseguire passaggi in inferenza rispetto alle lunghe foreste di regressione, com'è possibile combinare le velocità delle prime con l'affidabilità delle seconde?. La soluzione proposta dalla letteratura è una metodologia definita **Ranking Distillation**. Il principio razionale si basa sull'accettazione fiduciaria dell'accuratezza altissima di LambdaMART, allo scopo però di addestrare una DNN che ne imiti semplicemente l'output a *run time*. In sintesi, un'architettura a rete neurale artificiale (ANN) viene costretta ad approssimare pedissequamente gli output prodotti in partenza da un algoritmo LambdaMART, ignorando totalmente i *training labels* originali utilizzati in precedenza. Ai fini addestrativi, lo spazio campionario del dataset subisce un arricchimento focalizzato attorno a zone di chiara discontinuità matematica, ovvero gli *split points* degli alberi da imitare. Sono stati eseguiti test su modelli neurali *Fully Connected*, in particolare varianti a 4 strati (con dimensioni $2000 \times 500 \times 500 \times 100$) e versioni più leggere a soli 2 strati ($500 \times 100$). I test in termini di metrica di precisione media MAP dimostrano i seguenti valori:

| **Method**                                                                                               | **# Layers** | **MAP (MSN30k)** | **MAP (GOV2)** |
| -------------------------------------------------------------------------------------------------------- | ------------ | ---------------- | -------------- |
| Regression Forest                                                                                        | -            | 0.6004           | 0.2995         |
| $N_{approx}$                                                                                             | 4            | 0.5950           | 0.2995         |
| $N_{approx}$                                                                                             | 2            | 0.5955           | 0.3007         |
| $N_{relevance}$                                                                                          | 4            | 0.5639*          | 0.2531         |
| *(Le reti neurali approssimanti N_{approx} competono molto bene contro la regression forest originale)*. |              |                  |                |

Un'indagine aggiuntiva sui tempi ha confermato questo guadagno di velocità, riscontrando differenze tra codice C++ autogenerato contenente ramificazioni continue di selettori condizionali ("If-Then-Else") contro esecuzioni lineari su TensorFlow (CPU) o inferenze basate su GPU con Pytorch. In PyTorch GPU, le esecuzioni arrivano a impiegare solamente 0.323 - 0.335 unità temporali (su 20.000 alberi con 64 foglie limitate).

### Il Collo di Bottiglia nel Single-Stage Ranking

Sebbene i modelli matematici siano complessi, la vera sfida pratica sopraggiunge al momento del calcolo. Immaginare un ecosistema a stadio singolo, definito **Single-Stage Ranking**, significa concettualizzare un passaggio unico e diretto: una richiesta utente (Query + Matching Docs) che viene somministrata istantaneamente al Ranker intero, il quale produce a stretto giro l'ordine dei risultati definitivi. Questa logica richiede che l'algoritmo addestrato venga calcolato rigorosamente per ciascun documento nel sistema, e allo stesso tempo obbliga a estrarre massivamente tutti i vettori e le relative feature associate. Un simile approccio su scala produttiva risulta del tutto irrealizzabile per ragioni di efficienza e latenza.

[INSERIRE IMMAGINE: Illustrazione semplificata della struttura "Single-Stage Ranking", formata da tre soli blocchi: input query, processo nel Ranker singolo e output Results].

Questo vicolo cieco introduce le tre maggiori compromissioni o trade-off tra efficienza computazionale ed efficacia applicativa (efficacy) nel Learning to Rank. Il primissimo attrito è dato dal **Feature Computation Trade-off**, che costringe i tecnici a valutare accuratamente un delicato bilanciamento di costo tra impiegare *feature* lente, onerose ed eccezionalmente discriminanti in opposizione all'adozione esclusiva di estrazioni computazionalmente molto blande ma con scarso o nullo potere di differenziazione informativa.

### Il Design del Multi-Stage Ranking

Al fine di superare queste muraglie prestazionali, l'industria impiega le logiche architetturali di un ordinamento basato su segmentazione nota come **Multi-Stage Ranking**. Al cuore del sistema risiede un furbo trucco ingegneristico chiamato *Pipelined Ranking Architecture* per estrarre la selezione dei documenti top-k. Questa intelaiatura si realizza frequentemente impostando un design di livello primario a doppio step noto come **Two-Stage Ranking**. Iniziando dallo *STAGE 1*, un Ranker di base supportato da rapidi ed essenziali indici invertiti prenderà in consegna le elaborazioni dei match producendo una prima risposta improntata al puro Recall (Recall-oriented Ranking) al costo di calcolo più tenue possibile. Questo passaggio screma immediatamente il calderone producendo una prima manciata costituita dai soli "N candidati migliori", dove chiaramente la grandezza $N$ è immensamente maggiore dell'obiettivo finale $k$ ($N \gg k$). Soltanto su questo bacino ridotto saranno calcolate e consumate estrazioni complesse, alimentando il fatidico *STAGE 2* (Top Ranker guidato da potenti procedure automatizzate di apprendimento) dove il sistema agirà per estrema Precisione per fornire solo i migliori K docs e i risultati alla view dell'utente (Result Pages). Questo espediente assicura il rinvio di carichi troppo sbilanciati alle sole frazioni realmente candidate al punteggio vincente.

[INSERIRE IMMAGINE: Flowchart del "Pipelined Ranking Architecture" dove i documenti passano dal First Stage (orientato ad alta Recall tramite un Inverted Index) a un selettivo Second Stage (basato su moduli Learning to Rank guidati a ottimizzare la Precision dei dati risultanti)].

Ma sorge inevitabilmente un problema, come stabilire questa fetta intermedia, questa "K" candidabile?. Qui prende forma il secondo compromesso per progettisti denominato **Number of Matching Candidates Trade-off**. Prevedere una vasca generosa e spropositata di candidati promuoverà sì risultati altamente qualitativi ma incrementerà vertiginosamente i costi, mentre una scrematura esigua o aggressiva abbasserà decisamente l'overhead del sistema producendo all'opposto esiti deludenti e insoddisfacenti (low-quality results). Tra gli esempi canonici, dataset come Gov2 propongono di filtrare a soli 1000 documenti e l'analisi Clue Web09-B tende in genere su stime dai 1000 ai 2000 matching. Esistono poi implementazioni su estesa scala corporativa dove le scremature richiedono bacini operativi da centinaia e migliaia di candidati sparsi orizzontalmente in elaborazioni parallele presso flotte di svariate centinaia di *machines* contemporanee.

Andando oltre la dicotomia del doppio passo, si fa impiego di pipeline **Multi-Stage Ranking a 3 stadi** (STAGE 1, 2, 3), inserendo una fase preposta al *Contextual Ranking* sui migliori e strettissimi 30 risultati. L'intento della contestualità (che per definizione inerisce lo specifico set visualizzato) risiede nell'usare per il ranking variabili strettamente connesse con misure statistiche trasversali in output (medie ponderate, varianza, distribuzioni dei feature sets) in tandem con modelli basati sull'estrazione dei topic dominanti (topic model similarity). Si tenga nota che affinché l'esperienza visiva non crolli a latenze sgradevoli, il *First* e il *Second Stage* devono tipicamente essere interamente gestiti al momento esecutivo o distribuito sul medesimo snodo di interazione per l'utenza finale (serving node). Un design scalabile solleva svariate domande cruciali su *quali modelli applicare, su quante feature poggiare ogni segmento di condotto, ed in quanti documenti quantificarli via via*. L'osservazione su circa 200 configurazioni possibili ha attestato le massime performance settando condotti a 3 traguardi in cui le finestre di imbuto calavano dai primordiali passaggi per sfumare da 2500 candidati iniziali passando a 700 docs nel tratto conclusivo. Queste complesse dinamiche rendono oramai l'adeguamento stesso del dimensionamento $k$ del primo step o la ricerca del *processing pipeline* più funzionale, parte attiva in appositi cicli predittivi inseriti fin dentro ai passaggi di pre-calcolo del *training time*.

In sintesi un'architettura completa bilancia fluidamente tutti gli step integrando il terzo ed ultimo trade-off, denominato **Model Complexity Trade-off**. In una pipeline intelligente come il Multi-Stage Ranking, i progettisti sfumano dai classificatori semplicissimi ed eccezionalmente lesti per il recupero grezzo e a bassa pretesa quantitativa (impiegando Coordinate Ascent, Ridge Regression, o implementazioni di RankBoost e SVM-Rank ) salendo gradatamente ai colossi analitici densi (come Inizializzazioni GBRT pure, i classici GBRT o alberi Random Forest ) preposti alle decisioni chirurgiche. Con tali parametri in testa, in base a un progressivo stadio $i$, un modulo generico STAGE $i-1$ (Cheap Ranker) fornirà basi rapide per lo STAGE $i$ (Accurate Ranker implementato via ListNet o modelli medi come Oblivious Lambda-Mart), destinando solamente all'apice finale dello STAGE $i+1$ (Very Accurate Ranker) la rifinitura suprema di pochi mirati elementi in uscita.

---

### Glossario e Concetti Chiave

1. **LambdaMART**: Modello List-wise all'avanguardia che corregge i gradienti (tramite Lambda Gradients) sulla base di uno scambio virtuale tra due documenti unito allo scostamento in valore assoluto della metrica NDCG generata ($|\Delta NDCG|$).

2. **Ranking Distillation**: Pratica in cui una più svelta e duttile architettura deep (Reti Neurali) viene educata ad approssimare pedissequamente gli score ottimali creati dai colossi di regression forest (come LambdaMART), aggirando il collo di inferenza computazionale proprio per via del guadagno hardware della rete su GPU.

3. **Pipelined / Multi-Stage Ranking**: Paradigma ad imbuto in cui una query massiva incontra strati crescenti e consequenziali di filtri e Ranker di affinazione a Precisione incrementale. Passaggi a imbuto restringono o allargano dinamicamente lo spazio di interazione, separando brutalmente i calcoli faticosi da documenti scarsamente promettenti limitandone le esecuzioni sulle frange top.

4. **Trade-offs Operativi**: L'essenziale trilogia di bilanciamento affrontata per evitare le stagnazioni tipiche da Single-Stage. I bilanciamenti richiedono di armonizzare in modo dinamico i pesi e i profitti per **Costo delle Features Calcolate** , la **Taglia del Set dei Candidati** in corsa (Number of Candidates) , sfumando la **Complessità Matematica Inerente ai Modelli** da scaglioni economici (Cheap) ad algoritmi raffinatissimi (Very Accurate).

---

# Slide 8: Appunti di Information Retrieval: Trade-off tra Efficienza ed Efficacia

Questa sezione esplora il delicato equilibrio tra l'accuratezza dei risultati di ricerca e i costi computazionali associati, concentrandosi in particolare sull'ottimizzazione dei modelli di apprendimento basati su alberi decisionali.

### Il Calcolo Online della Rilevanza

Il processo base per calcolare la rilevanza di un documento per una data query si articola in una serie di passaggi sequenziali. Inizialmente, un estrattore di feature elabora i token del documento (di lunghezza $m$) e della query (di lunghezza $n$) per produrre un vettore di feature estratte manualmente. Questo vettore, che modella la rilevanza della specifica coppia query-documento, viene poi fornito in input al modello di ranking (Ranking Model) insieme alle relative etichette (labels), il quale infine emette un punteggio di rilevanza finale $s_{q,d}$. Attualmente, l'approccio *state-of-the-art* si basa su foreste composte da migliaia di alberi di regressione: sebbene garantiscano un'alta qualità dei risultati, il loro impiego risulta computazionalmente molto oneroso.

[INSERIRE IMMAGINE: Diagramma di flusso dell'Online Computation, che illustra il percorso dal documento e la query attraverso il Feature Extractor e il Ranking Model fino allo score di rilevanza.]

### Efficienza nel Machine Learning per l'IR

I modelli basati sul *machine learning* pensati per un ranking orientato alla precisione si fondano prevalentemente su ensemble di alberi, noti come **Tree forests** (ad esempio GBRT, LambdaMART, Random Forest e Oblivious Trees). Questi sistemi operano unendo l'output di un insieme di *weak learners*, dove ciascun albero contribuisce al punteggio totale con un punteggio parziale. La formula che definisce il punteggio complessivo $s(d)$ di un documento è data dalla somma dei punteggi parziali di ogni albero $T_i$: $s(d)=\sum_{i=1}^{n}s_{i}$. Il costo computazionale deriva dal fatto che, al momento dello *scoring*, ogni albero deve essere elaborato in modo del tutto indipendente. Per dare un'idea dell'onere, supponendo di avere 1.000 documenti per singola query e un modello con 3.000 alberi con una profondità di 10 nodi, il sistema deve eseguire 30.000 test per ogni documento ($3.000 \times 10$), traducendosi nell'impressionante cifra di 30 milioni di test per singola query.

### Anatomia di un Albero di Decisione

Strutturalmente, questi modelli ad albero valutano la rilevanza attraverso due componenti principali. I nodi interni rappresentano delle condizioni booleane specifiche sulle feature $f$, applicando una certa soglia (threshold). Le foglie (nodi terminali), invece, contengono la vera e propria predizione del valore di rilevanza. Come anticipato, il punteggio finale di rilevanza assegnato a una coppia query-documento equivale alla somma di tutte le singole predizioni generate dagli alberi del modello. I pesi e i tagli di questi nodi (come si evince dall'addestramento, ad esempio su feature come $F_1, F_2, F_3, F_4, F_6, F_8$) determinano come gli alberi vengono adattati (fitted) ai dati.

### Architettura a Cascata dei Motori di Ricerca

Per gestire l'immensa mole di dati, i moderni motori di ricerca suddividono il lavoro in due stadi principali mediante un'architettura a cascata. Il **Primo Stadio (Stage 1)** si basa su un approccio orientato alla *recall* (Recall-oriented Ranking). Ricevendo la query dell'utente, un Base Ranker accede a un Inverted Index per recuperare rapidamente un ampio set iniziale di $N$ documenti pertinenti (Matching Docs). Successivamente, il **Secondo Stadio (Stage 2)** prende in carico i top-N documenti e applica un approccio più sofisticato orientato alla *precisione* (Precision-oriented Ranking). In questa fase interviene il Top Ranker, che sfruttando funzionalità avanzate (Features) e algoritmi di Learning to Rank (LtR), seleziona un sottoinsieme estremamente ristretto di $K$ documenti finali, costituendo la pagina dei risultati mostrata all'utente.

### Linee di Ricerca sul Trade-off Efficienza/Efficacia

L'efficienza nei sistemi LtR viene affrontata secondo tre principali filoni di ricerca. Il primo riguarda l'ottimizzazione dell'efficienza direttamente all'interno del processo di apprendimento (*learning process*). Il secondo esplora il calcolo approssimato dei punteggi e l'utilizzo di strutture a cascata efficienti. Infine, il terzo si focalizza sull'attraversamento veloce e ottimizzato dei modelli tree-based già costruiti. Ognuno di questi approcci genera impatti significativamente diversi sull'architettura complessiva di ranking, influenzando sia l'addestramento (Training Data e Learning to Rank Technique) sia l'applicazione finale del modello sui campioni (K docs).

### Ottimizzare l'Efficienza durante l'Apprendimento: Il Modello MEET

Nel 2010, Wang, Lin e Metzler hanno proposto un metodo rivoluzionario per imparare a fare ranking in modo efficiente. La loro soluzione introduce una nuova funzione di costo che ottimizza direttamente una metrica specifica per il trade-off, chiamata **Efficiency-Effectiveness Tradeoff Metric (EET)**. La formula matematica alla base dell'EET per una determinata query è strutturata come segue:

$$EET(Q)=\frac{(1+\beta^{2})\cdot(\gamma(Q)(\sigma(Q))}{(\beta^{2}\cdot)\gamma(Q)+\gamma(Q)}\Rightarrow MEET(R)=\frac{1}{N}\sum EET(Q)$$

Il lavoro si concentra su funzioni di ranking lineari basate su feature e introduce nuove metriche di valutazione dell'efficienza con andamenti a decadimento costante, a gradino (step function), e a decadimento esponenziale. Addestrando i modelli con questa tecnica, gli autori hanno dimostrato una drastica e significativa diminuzione dei tempi medi di esecuzione delle query.

[INSERIRE IMMAGINE: Grafico del modello di Wang et al., che mostra le curve della metrica di Efficienza rispetto al Tempo di Ranking (ms) per le diverse funzioni di decadimento.]

### Cost-sensitive Tree Induction per GBRT (AL13)

Asadi e Lin, nel 2013, si sono spinti oltre presentando delle tecniche specifiche per addestrare foreste GBRT garantendo ottime prestazioni in fase di esecuzione (runtime). Il principio cardine è semplice: creare alberi più bilanciati, superficiali (shallow) e compatti velocizza intrinsecamente le predizioni. Questo risultato è raggiunto tramite la **Cost-sensitive Tree Induction**, una procedura che minimizza congiuntamente la perdita di efficacia e il costo della valutazione. Le strategie previste sono due:

1. Modificare direttamente il criterio di divisione (*splitting*) dei nodi durante la creazione dell'albero: viene consentita una divisione con guadagno massimo solo se questa non aumenta la profondità complessiva della struttura. In alternativa, l'algoritmo cerca un nodo più vicino alla radice che, se diviso, produrrebbe un guadagno superiore al guadagno massimo scontato.

2. Effettuare il **pruning** (potatura) contestualmente al boosting, concentrandosi primariamente sulla profondità e sulla densità dell'albero. Ciò avviene collassando forzatamente i nodi terminali finché il numero di nodi interni non restituisce un albero perfettamente bilanciato. Eventuali cali nell'efficacia del modello vengono poi compensati tramite l'aggiunta di stadi successivi.

Esperimenti condotti sul dataset MSLR-WEB10K indicano che la strategia basata sul pruning è la più performante, raggiungendo una diminuzione del 40% della latenza di predizione a fronte di una riduzione solo trascurabile del punteggio finale NDCG.

### CLEAVER: Ottimizzazione Post-Apprendimento

Un approccio alternativo all'efficienza è il post-processing, esplorato da Lucchese et al. nel 2016 con la metodologia **CLEAVER**. A differenza dei metodi di addestramento sensibili ai costi, CLEAVER interviene su un ensemble di alberi già addestrato applicando una combinazione di pruning e riponderazione dei pesi (re-weighting) tramite una strategia avida di ricerca lineare (*greedy line search*). Il framework offre svariate strategie di potatura, tra cui la rimozione casuale (random), degli ultimi alberi (last), il salto (skip), l'eliminazione dei pesi bassi (low weights), e la rimozione basata sulla perdita di score (score loss) o sulla perdita di qualità (quality loss). Sperimentato sulle architetture MART e LambdaMART tramite i dataset MSLR-Web30K e Istella-S LETOR, il risultato più significativo riguarda la strategia basata sulla *quality loss*, la quale permette di preservare esattamente la medesima efficacia del modello originario eliminando un numero di alberi tale da conservarne solo fino al 20% del volume iniziale.

---

### Concetti Chiave

- **Tree Forests (GBRT/LambdaMART)**: Ensemble complessi di alberi decisionali utilizzati per il ranking orientato alla precisione. Offrono un'eccellente efficacia ma presentano onerosi colli di bottiglia computazionali al momento dell'esecuzione.

- **Architettura a Cascata**: Modello di sistema di ricerca diviso in due fasi sequenziali. Una prima fase estrae un ampio numero di documenti orientandosi alla *recall*, seguita da una seconda fase che utilizza algoritmi più complessi orientati alla *precisione*.

- **Cost-sensitive Tree Induction**: Tecnica in cui l'addestramento dei modelli tiene conto contemporaneamente degli errori di previsione e dei costi computazionali per forzare la generazione di alberi compatti e superficiali.

- **Pruning (Potatura)**: Metodologia di riduzione della grandezza di un modello (applicabile in fase di learning o in fase di post-processing, come nel caso di CLEAVER) utile a scartare le porzioni di alberi superflue per abbattere drasticamente la latenza di calcolo mantenendo stabile la metrica NDCG.

---

### X-CLEAVER: Potatura Integrata nell'Addestramento

Sulla scia dell'ottimizzazione degli ensemble, nel 2018 Lucchese, Nardini, Orlando, Perego, Silvestri e Trani hanno presentato **X-CLEAVER** sulla rivista ACM TIST. Questo framework fa un passo avanti rispetto al suo predecessore introducendo il *pruning* e la riponderazione dei pesi direttamente durante la fase di apprendimento basata sul *gradient boosting*. Il processo si articola in due passaggi chiave: in primo luogo, gli alberi identificati come ridondanti vengono rimossi dall'ensemble in costruzione; successivamente, i pesi degli alberi superstiti subiscono un'operazione di *fine-tuning* mirata a ottimizzare direttamente una specifica metrica di qualità del ranking, come l'NDCG.

Il sistema riutilizza le medesime strategie di potatura del precedente metodo CLEAVER, ma l'impatto architetturale è radicalmente diverso. Gli esperimenti, condotti su dataset pubblici di riferimento come MSN30K-1 e Istella-S, hanno evidenziato che eseguire il pruning e il re-weighting *durante* l'apprendimento risulta nettamente più efficace rispetto all'applicazione di un singolo step di ottimizzazione *post-learning*. Di conseguenza, X-CLEAVER permette di addestrare foreste ancora più compatte garantendo al contempo nessuna perdita in termini di prestazioni globali.

[INSERIRE IMMAGINE: Grafico delle performance di testing di X-CLEAVER, che confronta la metrica NDCG@10 rispetto alle dimensioni dell'ensemble tra il modello X-CLEAVER e un modello $\lambda$-MART tradizionale.]

### DART: Il Dropout incontra i Regression Trees

Nel 2015, i ricercatori Rashmi e Gilad-Bachrach hanno introdotto l'algoritmo **DART** (pubblicato in PMLR), importando la fortunata tecnica dei *dropouts*, tipica delle reti neurali, nel contesto del Multiple Additive Regression Trees (MART). L'obiettivo primario di questa soluzione è combattere la tendenza dei modelli a sviluppare una **sovra-specializzazione** (*over-specialization*). Sebbene in passato si utilizzasse la tecnica dello *shrinkage* per mitigare questo problema, essa forniva un aiuto senza tuttavia costituire una soluzione definitiva.

DART differisce dal tradizionale MART per due meccanismi fondamentali. In primo luogo, durante la fase in cui il modello sta imparando a costruire un nuovo albero, un sottoinsieme casuale dell'intera struttura viene "silenziato" (*muted*). In secondo luogo, viene applicato uno step di normalizzazione nel momento in cui il nuovo albero viene aggiunto al sistema, così da prevenire il fenomeno dell'**overshooting**. Valutato sul dataset MSLR-Web10K focalizzandosi sulla metrica NDCG@3, DART ha fatto registrare notevoli miglioramenti prestazionali rispetto allo standard LambdaMART.

[INSERIRE IMMAGINE: Grafico della predizione media rispetto all'indice dell'albero, che illustra visivamente il comportamento di DART rispetto a un modello MART standard con e senza shrinkage.]

### X-DART: Fusione tra Dropout e Pruning

Partendo dal successo di DART, Lucchese, Nardini, Orlando, Perego e Trani hanno proposto **X-DART** alla conferenza ACM SIGIR del 2017. Questa evoluzione fonde elegantemente le logiche del dropout con le pratiche di pruning durante l'addestramento. In maniera analoga a DART, anche in X-DART determinati alberi vengono temporaneamente silenziati, ma la peculiarità risiede nel fatto che questo set di nodi viene successivamente rimosso del tutto dopo la fase di *fitting*, qualora ritenuto non necessario.

Questo approccio comporta due importanti vantaggi architetturali. Il primo è che X-DART genera modelli ancora più compatti di DART stesso. Il secondo vantaggio, derivante direttamente dalle ridotte dimensioni del modello, consiste in una minore propensione all'overfitting, il che sblocca il potenziale per raggiungere livelli di efficacia nettamente superiori. Per gestire il processo di rimozione, X-DART prevede tre diverse strategie di potatura: **Ratio**, **Fixed** e **Adaptive**. Le sperimentazioni sui dataset MSLR-Web30K e Istella-S hanno confermato che la variante X-DART (adaptive) fornisce miglioramenti statisticamente significativi rispetto al DART originale, riuscendo a impiegare fino al 20% di alberi in meno. Ancora più impressionante è la capacità del modello di eguagliare la medesima efficacia di DART utilizzando un monte alberi decurtato fino al 40%.

[INSERIRE IMMAGINE: Grafico che analizza l'andamento dell'NDCG@10 al variare della dimensione dell'ensemble, mettendo a confronto l'efficacia dei modelli DART rispetto alle diverse strategie di pruning (Ratio, Fixed, Adaptive) del modello X-DART.]

### Calcolo Approssimato dello Score e Cascate Efficienti

Oltre all'ottimizzazione degli alberi stessi, un filone di ricerca parallelo mira al calcolo approssimato dei punteggi mediante l'utilizzo di strutture a cascata molto rapide. Nel 2010, alla conferenza ACM WSDM, Cambazoglu e il suo team (Zaragoza, Chapelle, Chen, Liao, Zheng, Degenhardt) hanno formalizzato il concetto di ottimizzazione tramite "uscite anticipate" (**early exits**) per i sistemi di ranking basati sul machine learning additivo.

La logica dietro a questa strategia del "cortocircuito" in fase di calcolo (*short-circuiting*) si basa su considerazioni empiriche relative all'Information Retrieval. Per ogni singola query sottomessa, esiste solitamente solo una manciata di documenti realmente molto rilevanti, annegati in una moltitudine di risultati totalmente irrilevanti; inoltre, è noto che la stragrande maggioranza degli utenti non si spinge mai oltre la consultazione delle primissime pagine dei risultati. Di conseguenza, è superfluo processare tutti i documenti attraverso l'intero modello matematico. Per risolvere questa inefficienza, Cambazoglu et al. hanno introdotto ensemble additivi capaci di abortire il calcolo preventivamente. Questa interruzione intelligente è governata da quattro specifiche tecniche di soglia: soglie basate sul punteggio (**Score**), sulla capacità (**Capacity**), sul rango (**Rank**) o sulla prossimità (**Proximity**). Implementate all'interno di una piattaforma di machine learning all'avanguardia dotata di alberi GBRT , le ottimizzazioni tramite *early exit* hanno permesso al sistema di operare fino a quattro volte più velocemente rispetto all'algoritmo standard, il tutto senza riscontrare alcuna perdita qualitativa nei risultati proposti all'utente finale.

[INSERIRE IMMAGINE: Diagramma concettuale dell'Early Exit che illustra il percorso di un documento $d_i$ attraverso l'esecuzione sequenziale delle funzioni dell'ensemble ($f_1, f_2, f_3, f_4$), mostrando la possibilità di un'uscita anticipata dal calcolo contrassegnata come $e_2$.]

---

### Concetti Chiave

- **X-CLEAVER**: Evoluzione algoritmica che esegue il pruning e la calibrazione dei pesi direttamente *durante* l'addestramento tramite gradient boosting, creando fin dall'inizio modelli compatti e ad alta efficienza senza dover ricorrere a rielaborazioni successive.

- **DART e X-DART**: Tecniche all'avanguardia che silenziano casualmente parti degli alberi durante il training (dropout) per evitare l'eccessivo adattamento ai dati di addestramento (over-specialization/overfitting). X-DART si distingue per rimuovere permanentemente questi alberi "muti", riducendo enormemente la complessità del modello finale.

- **Early Exits (Uscite Anticipate)**: Strategia computazionale applicata agli ensemble additivi che blocca l'elaborazione del punteggio di rilevanza per quei documenti che mostrano precocemente un basso potenziale. Questo "cortocircuito" matematico previene lo spreco di risorse per risultati palesemente inutili, abbattendo drasticamente i tempi di latenza complessivi.

---

# Slide 9:# Addestramento e Inferenza Efficienti di Ensemble di Alberi di Decisione

In questo capitolo affronteremo il tema dell'addestramento e dell'inferenza efficienti per i modelli basati su **ensemble di alberi di decisione** (Ensembles of Decision Trees). Questo argomento, delineato dal professor Rossano Venturini dell'Università di Pisa, risulta fondamentale per comprendere a fondo le moderne architetture di Information Retrieval (IR) e i complessi sistemi di ranking.

### L'Architettura di Base e il Query Processing

Per inquadrare l'impiego pratico degli alberi di decisione, è essenziale prima osservare l'architettura generale di un motore di ricerca. L'intero processo si divide tipicamente in due grandi fasi: una fase **Offline** e una fase **Online**.

Durante la fase Offline, il sistema analizza una **Document Collection**. Questa passa attraverso una procedura di **Indexing** per arrivare alla costruzione del vero e proprio **Inverted Index**. Parallelamente, interviene un **Feature Processor** che ha il compito di estrarre le caratteristiche salienti, archiviandole all'interno di un **Document Features Repository**. Questi dati appena immagazzinati, combinati con i **Training Data**, vengono utilizzati nel processo di **Training** per generare un **Learning-to-rank Model**.

Nella fase Online, che si attiva nel momento in cui l'utente effettua una ricerca, una **Query** iniziale viene elaborata e trasformata in una **Expanded Query**. Questa query espansa viene inviata al blocco di **Query Processing**, il quale interroga direttamente l'Inverted Index. A questo punto, subentra la fase di **Feature Lookup and Computation**, che ha il compito di recuperare dal repository le caratteristiche specifiche associate ai documenti estratti. Infine, i documenti vengono ordinati da una **Learned Ranking Function**, la quale si basa sul modello di ranking precedentemente addestrato, restituendo così all'utente i risultati finali ordinati per rilevanza.

[INSERIRE IMMAGINE: Diagramma di flusso dell'architettura di un motore di ricerca, che illustra le fasi online e offline, i flussi dall'elaborazione della query alla feature computation, fino alla Learned Ranking Function e ai dati di training]

### La Gradient Boosting Machine (GBM)

Il motore matematico dietro molti di questi modelli di ranking è la **Gradient Boosting Machine (GBM)**. In termini generali, gli algoritmi di apprendimento **Boosting** operano addestrando i dati tramite molteplici **weak learners**. Un "weak learner" può essere inteso come un qualsiasi metodo di classificazione sotto-potenziato (under-power). La potenza del boosting risiede nel fatto che ogni nuovo learner apprende dagli errori commessi da quelli che lo hanno preceduto.

Nel caso specifico della GBM, il ruolo di weak learners viene ricoperto proprio dagli **alberi di decisione**. L'obiettivo finale di questa tecnica è imparare una complessa funzione $F(x)$, definita come la somma di $M$ weak learners. Questo concetto è riassunto dalla formula $F(x)=\sum_{i=0}^{M}F_{i}(x)$. La versatilità della GBM è notevole: può essere utilizzata efficacemente sia per compiti di regressione che per la classificazione, trovando un'applicazione perfetta anche nel Learning-to-Rank (LtR). Storicamente, questo approccio è stato originariamente proposto da Breiman nel 1997 e successivamente aggiornato e perfezionato da Friedman nel 1999. Oggigiorno, implementazioni altamente ottimizzate di questa teoria costituiscono il nucleo di librerie estremamente diffuse come **XGBoost** e **LightGBM**.

### Learning-to-Rank con Ensemble di Alberi di Regressione

Quando applichiamo i principi appena visti al Learning-to-Rank, il modello che ne deriva è descrivibile visivamente come una "foresta" di alberi di regressione. In questo ensemble, ogni singolo albero di decisione contribuisce fornendo un punteggio parziale. Di conseguenza, lo score finale attribuito a un determinato documento sarà dato dalla semplice somma matematica di tutti questi punteggi parziali.

Tuttavia, c'è un rovescio della medaglia dal punto di vista dell'efficienza: al momento del calcolo del punteggio (scoring time), il sistema è costretto a elaborare e attraversare tutti gli alberi presenti per ogni singolo documento. Per comprendere la scala del problema, analizziamo qualche numero: un modello tipico include un quantitativo di alberi di decisione $M$ che varia da $1K$ a $20K$ (da 1.000 a 20.000 alberi). Ognuno di questi alberi presenta dalle 16 alle 512 foglie, e l'intero sistema coinvolge un bacino di feature compreso tra 100 e 2000. In questo schema logico, alberi multipli (come $T_1$, $T_2$, fino a $T_n$) ricevono in input la coppia composta dalla query e dal documento $(q, d)$. Ciascun albero emette in uscita un sotto-punteggio ($s_1$, $s_2$, fino a $s_n$). Il punteggio globale del documento si calcola, come detto, tramite la sommatoria $s(d)=\sum_{i=1}^{n}s_{i}$.

[INSERIRE IMMAGINE: Schema concettuale che mostra diverse strutture ad albero indipendenti che processano la stessa coppia (q, d) e i cui output parziali confluiscono in una somma finale per ottenere il punteggio s(d)]

Per concretizzare la valutazione di uno di questi alberi di decisione, consideriamo un esempio pratico. Supponiamo di trovarci di fronte al seguente set di feature estratte per la coppia Query-Documento:

| **F1​** | **F2​** | **F3​** | **F4​** | **F5​** | **F6​** | **F7​** | **F8​** |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| 13.3    | 0.12    | -1.2    | 43.9    | 11      | -0.4    | 7.98    | 2.55    |

L'attraversamento inizia dalla radice, la quale imposta una condizione sulla feature $F_4$ (ad esempio, se il valore è $\le 50.1$). Poiché $F_4 = 43.9$, la condizione è soddisfatta e l'algoritmo si sposta verso il nodo successivo, che analizza la feature $F_1$ (soglia a $10.1$). Avendo $F_1 = 13.3$, la condizione non è superata e si imbocca il ramo alternativo fino ad arrivare alla feature $F_3$ (soglia a $-1.0$). Essendo $F_3 = -1.2$, si segue l'ultimo ramo che conduce alla foglia di uscita ("Exit leaf"). In questo esempio, la foglia di uscita ha un valore assegnato di $2.0$, pertanto il sistema incrementerà il punteggio totale applicando la regola $Score += 2.0$.

[INSERIRE IMMAGINE: Struttura di un albero di decisione con percorsi ramificati basati su soglie (es. 50.1:F4, 10.1:F1). Il percorso valutato nell'esempio precedente è contrassegnato con frecce rosse fino ad arrivare al nodo foglia con valore 2.0]

### Dettagli di Addestramento della Gradient Boosting Machine

Garantire un addestramento efficiente per questi ensemble richiede basi matematiche rigorose. L'algoritmo riceve in input un set di dati descritto come $\{(x_{i},y_{i})\}_{i=1}^{n}$, dove $x_{i}$ rappresenta il vettore delle feature e $y_{i}$ rappresenta il valore target. Deve essere definita inoltre una **funzione di perdita differenziabile** $L(y_{i},F(x_{i}))$. Se l'obiettivo è la regressione, si impiega generalmente l'Errore Quadratico Medio o **MSE (Mean Squared Error)**, formalizzato come $L(y_{i},F(x_{i}))=\frac{1}{2}(y_{i}-F(x_{i}))^{2}$. Al contrario, se ci si trova di fronte a un problema di classificazione, la scelta ricade sull'entropia incrociata (Cross entropy).

Il primo passo pratico consiste nello stabilire una predizione iniziale. Per minimizzare l'MSE fin dall'inizio, il sistema assegna come stima approssimativa di partenza la media aritmetica di tutti i valori target disponibili, espressa come $F_{0}(x_{i})=\frac{1}{n}\sum_{i=1}^{n}y_{i}$. Avendo stabilito questa baseline, inizia un ciclo iterativo che si ripete per $m \in [1,M]$, con $M$ pari al numero di weak learners che vogliamo costruire.

In ogni iterazione, l'obiettivo è elaborare i cosiddetti **pseudo-residui** (che corrispondono al gradiente, cioè le derivate parziali della funzione di perdita).

[RIFERIMENTO VISIVO DEL PROFESSORE: Viene mostrato un grafico cartesiano che traccia il valore predetto $F_m(x_i)$ contro la curva convessa della funzione di perdita $L(y_i, F(x_i))$. Il gradiente corrisponde alla pendenza della tangente alla curva, che indica la direzione per minimizzare l'errore]

Il gradiente rappresenta la derivata della funzione di perdita calcolata rispetto alla predizione corrente. Lo scopo è "spostare" questa predizione in una direzione tale da far decrescere la funzione di perdita.
In termini formali, lo pseudo-residuo per ogni punto $i$ da $1$ a $n$ si ricava così: $r_{i,m}=-[\frac{\partial L(y_{i},F(x_{i}))}{\partial F(x_{i})}]F(x)=F_{m-1}(x)$. Se si applica la logica dell'MSE, l'equazione si semplifica notevolmente in $r_{i,m}=y_{i}-F_{i}(x_{m})$.

Una volta quantificati i pseudo-residui, si adatta un nuovo albero di regressione proprio su questi scarti, andando a creare un numero di regioni terminali (le foglie) pari a $k_{m}$. Per ciascuna di queste regioni $j$, si prende il punto dati $x_{i}$ che cade al suo interno e si procede calcolando il valore di $\gamma$ ottimale che vada a minimizzare l'errore secondo questa formula: $\gamma_{j,m}=argmin_{\gamma}\sum_{x_{i}}L(y_{i},F_{m-1}(x_{i})+\gamma)$. Nel contesto dell'MSE, questo valore matematico risulta essere nient'altro che la media dei valori target presenti in quella specifica regione terminale.

Il passo conclusivo dell'iterazione consiste nell'aggiornare l'intero modello sommando il nuovo componente individuato: $F_{m}(x)=F_{m-1}(x)+\alpha\gamma_{j,m}$. In questa espressione, $j$ fa riferimento alla regione che contiene il punto $x$, mentre $\alpha$ agisce come tasso di apprendimento (learning rate). Ripetendo questi step, al termine del ciclo l'algoritmo restituirà in output il modello definitivo $F_{M}$. Man mano che avvengono i "node split" (le divisioni dei nodi), l'accuratezza migliora, portando a una progressiva diminuzione della somma dei residui originari.

[INSERIRE IMMAGINE: Grafici a dispersione (scatterplot) sovrapposti che dimostrano visivamente come, partendo da una linea piatta che rappresenta la media $F_0$, ogni split dei nodi riduca l'ampiezza dei residui, ovvero la distanza dei punti blu dalla linea di approssimazione rossa]

### Concetti Chiave

1. **Inverted Index**: Struttura dati primaria elaborata offline che permette al motore di ricerca di recuperare documenti rapidamente al momento dell'invio di una query online.

2. **Weak Learners**: In ambito Boosting, si definiscono così i classificatori semplici (come un singolo albero di decisione non profondo) che, lavorando in sinergia, riescono a formare un modello di elaborazione molto potente.

3. **Gradient Boosting Machine (GBM)**: Sofisticata tecnica di apprendimento che ottimizza modelli di predizione in modo iterativo, dove ogni albero successivo si focalizza nel correggere gli errori (pseudo-residui) commessi nella fase precedente.

4. **Learning-to-Rank (LtR)**: L'applicazione degli algoritmi di ensemble (come la somma dei vari sotto-punteggi di una foresta di alberi) per determinare un punteggio totale capace di stabilire l'ordine di rilevanza per un insieme di documenti.

5. **Pseudo-residui e Gradiente**: I pseudo-residui indicano il delta di errore per ciascun punto dati, corrispondente al gradiente della funzione di perdita. Muovere le previsioni lungo questo gradiente consente di minimizzare l'errore del modello.

---

### L'Evoluzione con XGBoost

**XGBoost** è stato proposto da **Tianqi Chen** e **Carlos Guestrin** nel 2014. Questo algoritmo ha ottenuto un'enorme risonanza a metà degli anni 2010, affermandosi come la scelta prediletta da numerosi team vincitori nelle competizioni di machine learning. L'obiettivo principale di XGBoost è migliorare le implementazioni di GBM esistenti per permettere loro di scalare efficacemente in base alla quantità di dati di addestramento.

Le innovazioni introdotte da XGBoost per ottimizzare le performance includono:

- L'adozione di **approcci approssimati** per individuare il punto di split ottimale, superando i limiti della scansione esaustiva.

- L'utilizzo dell'**elaborazione parallela** per velocizzare la ricerca dei punti di divisione. È importante notare che non è possibile parallelizzare la creazione dei diversi alberi di decisione, poiché ognuno dipende dal precedente.

- Un sistema di accesso ai dati **cache-aware**, che ottimizza il modo in cui le informazioni vengono lette dalla memoria.

- L'integrazione di tecniche di **regolarizzazione** e **pruning** (potatura) che vengono applicate direttamente durante la fase di branching (ramificazione) dell'albero.

### L'Algoritmo Exact Greedy per la Ricerca dello Split

Per comprendere il funzionamento interno di un albero, analizziamo l'**Exact Greedy Algorithm**. Questo algoritmo ha il compito di trovare la divisione dei dati che massimizza il guadagno informativo, esaminando ogni possibile punto di split per ogni feature.

[INSERIRE IMMAGINE: Grafico a dispersione che mostra i punti dati su un piano cartesiano definiti dalle variabili Time e Project, con una linea rossa che indica una possibile divisione]

Per illustrare il processo, consideriamo un esempio pratico basato su sei studenti, di cui analizziamo le variabili **Time** (tempo) e **Project** (progetto) per predirne un valore target **y**:

| **Studente** | **Time** | **Project** | **y** | **r0​** |
| ------------ | -------- | ----------- | ----- | ------- |
| 2            | 10       | 1           | 18    | -6      |
| 1            | 20       | 2           | 30    | 6       |
| 6            | 30       | 1           | 21    | -3      |
| 4            | 30       | 2           | 25    | 1       |
| 3            | 40       | 4           | 30    | 6       |
| 5            | 50       | 3           | 20    | -4      |
|              |          |             |       |         |

Il calcolo inizia definendo la predizione iniziale $F_0$, che corrisponde alla media dei valori target $y$, ovvero $F_0 = 24$. Da questo valore si ricavano i residui iniziali $r_0$, ottenuti sottraendo la media ai valori reali (ad esempio, per lo studente 2: $18 - 24 = -6$). Il punteggio della radice, denominato **score_root**, viene inizialmente impostato a 0, poiché la somma dei residui iniziali è nulla.

L'algoritmo procede quindi a valutare i possibili candidati per lo split. Una delle condizioni testate è, ad esempio, **Time <= 15**.

[RIFERIMENTO VISIVO DEL PROFESSORE: Nelle slide dalla 13 alla 20, viene mostrato graficamente come l'algoritmo "Exact Greedy" scansiona sequenzialmente tutti i possibili valori delle feature. Una linea rossa si muove lungo gli assi del grafico "Time" e "Project" per testare ogni possibile divisione e calcolarne il relativo punteggio]

Durante questa scansione sistematica, per ogni divisione ipotizzata vengono calcolati due valori: lo **score_left** e lo **score_right**. Nel caso dello split su **Time <= 15**, lo studente 2 (residuo -6) viene isolato nel ramo sinistro, generando uno $score_{left} = 36$, mentre gli altri cinque studenti finiscono nel ramo destro, producendo uno $score_{right} = 6$. Questo metodo garantisce di trovare il punto di divisione matematicamente ottimale, ma risulta estremamente oneroso dal punto di vista computazionale perché costringe il sistema a enumerare ogni singola possibilità.

### Concetti Chiave

1. **XGBoost**: Algoritmo evoluto basato sul Gradient Boosting che introduce parallelismo e ottimizzazioni di memoria per gestire dataset massivi.

2. **Exact Greedy Algorithm**: Metodo di ricerca che scansiona ogni valore di ogni feature per individuare lo split migliore.

3. **Pseudo-residui ($r_0$)**: La differenza tra il valore target reale e la stima attuale, utilizzati come obiettivo per l'addestramento del prossimo albero nell'ensemble.

4. **Score (Root/Left/Right)**: Metriche numeriche utilizzate per valutare la qualità di una divisione dei dati all'interno di un nodo dell'albero.

---

### Calcolo del Guadagno e Costruzione dell'Albero

Proseguendo con l'esempio pratico basato sui dati degli studenti (variabili **Time** e **Project** rispetto al target **y**), l'algoritmo valuta un nuovo possibile punto di divisione. Nello specifico, il sistema testa la condizione **Project <= 1**.

Per comodità, richiamiamo la tabella dei dati analizzati:

| **Studente** | **Time** | **Project** | **y** | **r0​** |
| ------------ | -------- | ----------- | ----- | ------- |
| 2            | 10       | 1           | 18    | -6      |
| 1            | 20       | 2           | 30    | 6       |
| 6            | 30       | 1           | 21    | -3      |
| 4            | 30       | 2           | 25    | 1       |
| 3            | 40       | 4           | 30    | 6       |
| 5            | 50       | 3           | 20    | -4      |

Applicando la regola **Project <= 1**, i dati vengono divisi in due rami. Il ramo sinistro ("y", yes) accoglie gli studenti 2 e 6, i cui residui sono rispettivamente -6 e -3. Il ramo destro ("n", no) riceve i restanti studenti con residui 6, 1, 6 e -4.

[INSERIRE IMMAGINE: Albero di decisione parziale che mostra lo split sulla condizione "Project <= 1". Il ramo sinistro "y" porta a un nodo foglia con i valori -6 e -3, mentre il ramo destro "n" porta a un nodo con i valori 6, 1, 6 e -4.]

A questo punto, l'algoritmo calcola il punteggio per ciascun ramo. Il punteggio del nodo sinistro si ottiene quadrando la somma dei residui e dividendo per il numero di elementi: $score_{left} = \frac{(\sum r_0(i))^2}{2} = 40,5$. Seguendo la stessa logica, il punteggio del nodo destro risulta essere: $score_{right} = \frac{(\sum r_0(i))^2}{4} = 20,25$.

L'elemento cruciale per decidere se confermare questa divisione è il calcolo del **gain** (guadagno). La formula applicata è: $gain = score_{left} + score_{right} - score_{root} + \gamma$. All'interno di questa equazione, la variabile $\gamma$ rappresenta la **pruning constant** (costante di potatura). In questo specifico scenario, il valore della costante è posto a -0. Sostituendo i valori calcolati, il guadagno riportato dall'algoritmo ammonta a 42.

L'albero continua a espandersi ricorsivamente. Ad esempio, il ramo destro subisce una successiva diramazione basata sulla condizione **Time <= 45**. Al termine della costruzione, per ogni foglia terminale viene calcolato l'output finale, che corrisponde semplicemente alla media dei residui contenuti in quella foglia, secondo la formula: $output = \frac{1}{n} \sum r_i$. Grazie a questo calcolo, le tre foglie finali dell'esempio ottengono rispettivamente i valori di output **-4.5**, **4.3** e **-3**.

### I Limiti dell'Exact Greedy Algorithm

Nonostante l'approccio Exact Greedy garantisca di trovare matematicamente il punto di divisione perfetto, si rivela gravemente inefficiente per applicazioni su larga scala.

Il motivo di questa inefficienza risiede nel fatto che il sistema non è in grado di enumerare tutte le possibili divisioni (che possono arrivare fino a *n* split possibili per ogni singola feature) in tempi ragionevoli. Di conseguenza, il tempo di elaborazione richiesto per processare ogni singolo nodo cresce in maniera proporzionale, richiedendo un tempo pari a **(#feature $\times$ #data points)**.

### Introduzione all'Histogram-based Split Finding

Per superare il blocco computazionale appena descritto, i sistemi moderni abbandonano l'Exact Greedy in favore di un approccio noto come **Histogram-based Split Finding**.

[RIFERIMENTO VISIVO DEL PROFESSORE: Grafico a dispersione che mostra i punti dati distribuiti sugli assi "Time" e "Project". L'asse "Time" è suddiviso orizzontalmente in specifiche fasce di valore (0-20, 20-40, 40-60), anticipando visivamente il concetto di partizionamento a blocchi tipico degli istogrammi.]

L'idea fondamentale alla base di questo metodo è rinunciare alla precisione assoluta della scansione punto per punto, raggruppando invece i dati in segmenti più ampi per velocizzare drasticamente la ricerca dello split ottimale.

---

### Concetti Chiave

1. **Score e Gain**: Metriche matematiche fondamentali. Lo *score* valuta la purezza di un singolo nodo, mentre il *gain* quantifica il miglioramento complessivo apportato da uno split rispetto al nodo padre.

2. **Pruning Constant ($\gamma$)**: Un parametro utilizzato nel calcolo del guadagno che serve a controllare la complessità dell'albero; se il guadagno non supera questa soglia, lo split può essere "potato" (ignorato).

3. **Inefficienza dell'Exact Greedy**: Limite strutturale dell'algoritmo di base, il cui costo temporale scala moltiplicando il numero di feature per il numero di punti dati ($O(F \times N)$), rendendolo inadatto a dataset massivi.

4. **Histogram-based Split Finding**: Tecnica di ottimizzazione che mira a risolvere l'inefficienza dell'Exact Greedy, approssimando la ricerca degli split raggruppando i valori delle feature in istogrammi.

---

### La Ricerca degli Split basata su Istogrammi (Histogram-based Split Finding)

Come abbiamo precedentemente osservato, l'approccio exact greedy risulta estremamente oneroso, poiché richiede un tempo di calcolo proporzionale al numero di feature moltiplicato per il numero di punti dati per ogni singolo nodo elaborato. A causa di questo limite, non è materialmente possibile enumerare e testare tutte le possibili divisioni in scenari complessi , dato che ogni singola feature potrebbe presentare fino a *n* split potenziali. Per superare questo collo di bottiglia, l'informatica moderna impiega la tecnica dell'**Histogram-based Split Finding**. Questo metodo sfrutta gli istogrammi come strumento per aggregare valori consecutivi all'interno dei dati. Invece di analizzare ogni punto, i possibili valori assunti da una feature vengono raggruppati all'interno di un determinato numero di "bin" (contenitori virtuali); questa suddivisione può avvenire in modo uniforme oppure seguendo la distribuzione basata sui percentili dei dati. Di conseguenza, è fondamentale prevedere un aggiornamento tempestivo dell'istogramma ogniqualvolta si genera un nuovo nodo nell'albero decisionale , tenendo conto che questa partizione a blocchi può essere applicata sia a livello globale che locale.

[INSERIRE IMMAGINE: Istogramma a barre che mostra la somma dei residui suddivisa per specifici intervalli o bin predefiniti, nello specifico per le fasce 0-20, 20-40 e 40-60 ]

Grazie a questa categorizzazione per fasce, l'efficienza aumenta radicalmente: la ricerca di ogni split richiederà un tempo proporzionale unicamente al numero di feature moltiplicato per il numero di bin, saltando la scansione estenuante di ogni singolo record. Da un punto di vista tecnico, fissando un parametro *b* bit, i valori originali della feature vengono direttamente quantizzati all'interno di $2^b$ bin. Il vantaggio di tale ottimizzazione è cruciale per poter addestrare dataset di proporzioni gigantesche garantendo al contempo un impatto minimo sulla memoria del sistema (memory footprint). Basti pensare che, sfruttando questa tecnica, processare il dataset Higgs contenente 10 milioni di istanze su una GPU consuma solamente 611MB di RAM. Per fornire un quadro delle configurazioni standard, l'algoritmo imposta solitamente di default l'utilizzo di 255 bin, che vengono ridotti a 16 bin se si elabora tramite processore grafico (GPU).

[RIFERIMENTO VISIVO DEL PROFESSORE: Viene mostrato un istogramma a colonne che mette a confronto il tempo di addestramento misurato in secondi. Il grafico valuta dataset molto noti come Higgs, epsilon, Bosch, Microsoft-LTR, Expo e Yahoo-LTR. I test presentano le performance ottenute variando i bin a 255, 63 e 15, ed eseguendo le operazioni sia su una CPU da 28-Core sia su schede grafiche come AMD RX 480 e NVIDIA GTX 1080 ]

### L'Architettura Innovativa di LightGBM

L'algoritmo **LightGBM** è stato formalmente introdotto da Microsoft nel 2016, con il chiaro scopo di processare dataset enormi in maniera ancora più veloce di quanto facesse già XGBoost. Pur basando le proprie fondamenta sul concetto di Histogram-based split finding , gli sviluppatori si resero conto che la sola operazione di costruzione e continuo aggiornamento degli istogrammi restava comunque dispendiosa in termini di tempo, essendo sempre ancorata alla proporzione derivata da #features $\times$ #data points. Per abbattere anche questa barriera computazionale, LightGBM agisce contemporaneamente su due fronti: riduce drasticamente sia il numero delle istanze di dati da processare, sia il numero delle feature effettive. Questi due obiettivi vengono conseguiti attraverso due metodologie proprietarie: il campionamento **Gradient-based One-side Sampling (GOSS)** e il raggruppamento **Exclusive Feature Bundling (EFB)**.

### Riduzione dei Dati: Gradient-based One-side Sampling (GOSS)

La missione principale del GOSS è abbassare il quantitativo di punti dati che partecipano alla delicata fase di adattamento (fitting) dell'albero di regressione. È pratica comune per moltissimi algoritmi di ensemble learning eseguire un banale campionamento casuale uniforme (random sampling) per cercare di sveltire i calcoli. LightGBM stravolge questo approccio: evita il campionamento casuale puro e guida la selezione dei dati soppesando la dimensione del valore assoluto dei residui, che corrisponde essenzialmente al gradiente. Questa strategia nasce dall'intuizione che dati caratterizzati da gradienti nettamente diversi svolgano ruoli differenti quando si tratta di calcolare il guadagno informativo. Pertanto, tutte quelle istanze di dati che presentano un residuo piccolo (ovvero per cui il sistema si sta già sbagliando di poco) possono essere deliberatamente escluse dai passi successivi di addestramento, poiché le loro predizioni risultano essere già altamente affidabili.

Il meccanismo interno del GOSS si governa tramite due parametri: il parametro *a*, che definisce il tasso di campionamento dedicato esclusivamente ai dati con gradiente elevato , e il parametro *b*, destinato a indicare il tasso di campionamento per i dati con gradiente più basso. Prendiamo come esempio un panorama con $n=20$ istanze totali, assegnando artificialmente il valore $a=0.15$ e il valore $b=0.3$. Il protocollo impone per prima cosa di ordinare minuziosamente l'intero set di dati seguendo l'ordine del valore assoluto del residuo. Da questa graduatoria, l'algoritmo preleva un gruppo speciale chiamato TOP, formato esclusivamente dai $t = a \times n = 3$ punti associati ai valori residui più grandi in assoluto. A seguire, seleziona in modo rigorosamente casuale un secondo gruppo, denominato RANDOM, che in questo caso conterrà $l = b \times n = 6$ punti, scelti unicamente fra i punti scartati in precedenza. Per non sbilanciare l'apprendimento, LightGBM inietta una ponderazione matematica: tutte le istanze rientrate nel gruppo TOP ricevono un peso pari a 1 , mentre tutte le istanze del gruppo RANDOM ricevono un peso di compensazione calcolato esattamente con la formula $\frac{1-a}{b}$. Il passo finale consiste nell'addestrare il successivo albero della sequenza impiegando solamente questa manciata di punti (sia TOP che RANDOM) attentamente campionati e moltiplicati per il loro nuovo peso specifico $w$.

| **Time** | **Project** | **y** | **r0​** | **W** |
| -------- | ----------- | ----- | ------- | ----- |
| 10       | 1           | 18    | -6      | 1     |
| 20       | 2           | 30    | 6       | 1     |
| 40       | 4           | 30    | 6       | 1     |
| 50       | 3           | 20    | -4      | 2.83  |
| 30       | 1           | 21    | -3      | 2.83  |
| 30       | 2           | 25    | 1       | 2.83  |

(Tabella esemplificativa dei valori e dei pesi $W$ generati dal processo GOSS illustrato)

### Riduzione delle Feature: Exclusive Feature Bundling (EFB)

L'altra innovazione cruciale è l'**Exclusive Feature Bundling (EFB)**, concepita espressamente per abbattere l'enorme ammontare di feature attribuite a ciascun punto analizzato. I moderni set di dati ad alta dimensionalità sono contraddistinti da una spiccata sparsità; possiedono, cioè, una quantità esigua di componenti diverse da zero disseminate in colonne perlopiù vuote. Un'osservazione decisiva è che molte di queste feature sono reciprocamente esclusive: non si verifica quasi mai la condizione per cui due variabili simili assumano simultaneamente un valore diverso da zero. L'approccio EFB garantisce una tecnica virtualmente priva di perdita di dati (nearly lossless) strutturata appositamente per snellire questo panorama. Tale metodo fonde (tramite un bundle) queste feature esclusive, raggruppandole in totale sicurezza all'interno di una sola, nuova feature composita. Se è pur vero che l'algoritmo perfetto per partizionare tutte le feature nel minor quantitativo possibile di bundle sia matematicamente classificato come un problema NP-hard , l'implementazione in LightGBM risulta eccezionale per limare i tempi tecnici: una volta formati i cluster, valutare ogni nuovo split costerà al sistema un tempo dipendente semplicemente dal numero di bundle per il numero di bin (indicato come #buddles $\times$ #bins).

| **id** | **Midterms** | **Exam** | **M&E** |
| ------ | ------------ | -------- | ------- |
| 1      | 1            | 0        | 1       |
| 2      | 0            | 1        | 2       |
| 3      | 0            | 1        | 2       |
| 4      | 1            | 0        | 1       |
| 5      | 0            | 0        | 0       |
| 6      | 1            | 0        | 1       |

(Tabella che dimostra operativamente il concetto di EFB: le colonne esclusive 'Midterms' ed 'Exam' vengono raggruppate senza perdita di logica nella nuova e singola colonna 'M&E')

### Valutazione Sperimentale dei Modelli GBDT

L'impatto di simili architetture viene ampiamente validato dalla comunità accademica. Un punto di riferimento essenziale è lo studio "An Experimental Evaluation of Large Scale GBDT Systems", redatto dagli accademici Fangcheng Fu, Jiawei Jiang, Yingxia Shao e Bin Cui e pubblicato nel contesto del pVLDB nel 2019.

[RIFERIMENTO VISIVO DEL PROFESSORE: Si evidenziano i grafici dei risultati empirici tratti dal documento, che tracciano l'andamento della Valid AUC (misura di accuratezza) in funzione del tempo speso in secondi. Le curve presentate confrontano chiaramente le prestazioni dei sistemi Vero, DimBoost, LightGBM e XGBoost testati a fondo su scenari di enorme complessità, includendo i famosi dataset denominati (a) SUSY, (b) Higgs, (e) RCV1 e (f) Synthesis ]

### Verso l'Inferenza Efficiente: Panoramica e Complessità

Passando dalla teoria dell'addestramento all'applicazione reale, incontriamo il vasto argomento dell'**Inferenza Efficiente di Ensemble di Alberi di Regressione**. Come già menzionato, per orchestrare i modelli di Learning-to-Rank si dispiega una moltitudine di architetture, genericamente raggruppabili sotto la dicitura di "Foresta di alberi": vi rientrano, tra i vari, GBRT, Lambda MART, Random Forest e Oblivious Trees. Ognuno di questi agglomerati poggia sul principio di far collaborare centinaia o migliaia di "weak learners" (i singoli alberi di decisione), laddove ogni unità esprime un minuscolo contributo, ovvero un punteggio parziale. Lo score categorico assegnato infine a un documento equivale logicamente all'addizione completa di tutti questi sotto-punteggi.

La vera criticità emerge nel momento topico del calcolo del punteggio (il cosiddetto scoring time): per valutare l'effettiva pertinenza di un singolo elemento, il motore deve rigorosamente processare l'intera selva di alberi costruita durante la fase offline. Le metriche standard delineano uno scenario computazionalmente brutale: il numero totale di alberi impiegati, etichettato con la variabile $M$, conta frequentemente dalle 1.000 alle 20.000 unità (1K-20K). Ognuno di essi nasconde una struttura densa ramificata in un range che va dalle 16 alle 512 foglie finali. Tutto l'impianto viene alimentato da un vocabolario di parametri esteso tra le 100 e le 2000 feature processate costantemente. Matematicamente, una determinata combinazione originata da una query e dal relativo documento, denotata con $(q, d)$, transita attraverso i nodi $T_1$, $T_2$ fino al terminale $T_n$ generando i relativi frammenti numerici $s_1$, $s_2$ fino a $s_n$; frammenti che saranno poi inglobati nell'espressione ricapitolativa $s(d)=\sum_{i=1}^{n}s_{i}$ per decretare il posizionamento esatto in graduatoria .

[INSERIRE IMMAGINE: Illustrazione del flusso di inferenza parallela per una coppia (q, d) interrogata simultaneamente da alberi multipli (da T1 a Tn). Sotto ogni albero compaiono nodi rettangolari, dai quali scendono frecce tratteggiate verso i rispettivi risultati parziali s1, s2, sn che infine convergono su un'unica linea del punteggio totale ]

---

### Concetti Chiave e Glossario

1. **Histogram-based Split Finding**: Innovativa tecnica per raggruppare i valori di una feature in piccoli scaglioni ("bin" o istogrammi), riducendo drasticamente il costo della scansione dei dati durante l'apprendimento e rendendo obsoleto il dispendioso Exact Greedy Algorithm.

2. **LightGBM**: Imponente architettura ad albero strutturata originariamente da Microsoft, ideata per soverchiare XGBoost velocizzando il trattamento di masse enormi di dati tramite campionamenti predittivi mirati.

3. **GOSS (Gradient-based One-side Sampling)**: Modello di campionamento selettivo asimmetrico: preserva e preleva con probabilità massima tutte quelle istanze che esprimono un margine d'errore elevato (alto gradiente), pescando in misura minore dai cluster con dati ben classificati.

4. **EFB (Exclusive Feature Bundling)**: Filosofia che argina la "maledizione della dimensionalità" tipica dei database sparsi (sparse data); fonde variabili reciprocamente esclusive rendendole un unico vettore condensato quasi senza decurtazione di valore logico.

5. **Inferenza e Scoring Time**: La prova su strada post-addestramento. Durante lo scoring time un modello LtR calcola dal vivo l'aderenza di un documento sommando il contributo microscopico emesso individualmente da ogni singola "foglia" interpellata nell'intero bosco di migliaia di alberi del modello.

---

### I Problemi degli Approcci Tradizionali e la Naïve Baseline

Una volta addestrato un modello complesso, il calcolo del punteggio per un documento richiede l'attraversamento fisico di ogni singolo albero. L'approccio di base, noto come **Naïve baseline**, prevede che ogni nodo dell'albero sia rappresentato come un oggetto informatico contenente l'identificatore della feature (feature id), la soglia di riferimento (threshold) e i puntatori per muoversi verso il ramo sinistro o destro. A livello di codice, questa architettura si traduce tipicamente in classici blocchi condizionali "If-then-else". Il programma valuta una condizione, ad esempio `if (x[4] <= 50.1)`, e procede ricorsivamente sul sotto-albero sinistro in caso affermativo, o sul sotto-albero destro in caso contrario . Se si raggiunge una foglia, il sistema restituisce semplicemente un valore numerico, come ad esempio `return 0.4;` o `return -1.4;` .

+3

[INSERIRE IMMAGINE: Struttura condizionale If-then-else che modella il passaggio tra i nodi di un albero di decisione, in base a soglie predefinite per specifiche feature]

Questo meccanismo apparentemente lineare nasconde gravi falle prestazionali. In primo luogo, il sistema è costretto a "pagare" computazionalmente sempre per l'intera profondità dell'albero (depth of the tree). In secondo luogo, questi salti continui causano un alto tasso di errata predizione dei salti da parte del processore (**High branch misprediction rate**) e portano a un bassissimo tasso di hit nella cache di memoria (**Low cache hit ratio**). La comunità di ricerca ha tentato di proporre tecniche allo stato dell'arte (SoA) per arginare il problema, tra cui spiccano **Struct+** e **VPred**. Quest'ultimo, ad esempio, implementa funzioni ottimizzate per elaborare la profondità, come la funzione C-like `double depth4(float x, Node nodes)` che estrae l'identificatore del nodo e aggiorna sequenzialmente il suo indice calcolando il percorso sui figli per poi ritornare gli score finali . Ciononostante, i colli di bottiglia legati all'architettura hardware rimangono presenti.

+4

### L'Intuizione di QuickScorer: Oltre l'If-Then-Else

Per superare radicalmente queste inefficienze fisiche e logiche, è stato introdotto l'algoritmo **QuickScorer**. Il successo di questo metodo si fonda su due ingredienti principali: da una parte propone un attraversamento alternativo per ogni singolo albero (Alternative traversal of a single tree), dall'altra è in grado di processare l'intera foresta di alberi simultaneamente (Process the whole forest at once).

+1

Il funzionamento di QuickScorer durante l'attraversamento del singolo albero (Single Tree Traversal) abbandona completamente il concetto di navigazione gerarchica. Al contrario, l'algoritmo esamina le condizioni imposte dai nodi classificandole categoricamente in **True Node** (Nodo Vero) e **False Node** (Nodo Falso).

+2

[RIFERIMENTO VISIVO DEL PROFESSORE: Un albero decisionale colorato in verde e rosso per distinguere visivamente i nodi veri da quelli falsi, con le foglie numerate da 0 a 7, e affiancato da array di bit ("Result") che subiscono operazioni logiche per isolare il valore finale ]

+1

La genialità di QuickScorer risiede nell'utilizzo delle maschere per i nodi falsi (use of false nodes' masks). Ogni nodo dell'albero è associato a uno specifico vettore di bit (bitvector). L'algoritmo inizializza un vettore "Result" (Risultato) impostando tutti i bit a 1, come ad esempio la stringa `11111111` che rappresenta le 8 foglie terminali di un ipotetico albero. Successivamente, il sistema identifica esclusivamente i nodi la cui condizione non è soddisfatta (i nodi falsi) e applica un'operazione logica **AND** sequenziale tra il vettore "Result" e i bitvector associati a questi nodi. In virtù delle proprietà matematiche dell'operazione AND condotta su questi vettori (ad esempio combinando `00011111` e `11111101`), il risultato isola esattamente il bit corrispondente alla foglia finale corretta.

+4

Questo stratagemma matematico rende il processo totalmente **insensibile all'ordine di elaborazione dei nodi** (Insensitive to nodes' processing order!). Potendo demandare a un "oracolo" la semplice stesura della lista dei nodi falsi, l'algoritmo abbatte la necessità di eseguire istruzioni di salto, azzerando di fatto le "branches" (No branches) e annientando così il problema della branch misprediction.

+2

### Attraversamento Interlacciato e Disposizione dei Dati

Il secondo ingrediente fondamentale di QuickScorer è l'attraversamento interlacciato degli alberi (Interleaved tree traversals), che permette di processare la foresta in blocco. Per ottenere ciò, i dati non vengono più immagazzinati come alberi logici separati, ma vengono spacchettati in una serie di array contigui in memoria.

+1

Specificamente, le soglie di divisione vengono raggruppate per feature (da f0​, f1​ fino a f∣F∣−1​) e salvate all'interno di un grande array denominato `thresholds`, rigorosamente ordinate per valori crescenti (increasing values) . L'architettura prevede un array `offsets` di dimensione ∣F∣ che funge da indice, un array `tree_ids` per rintracciare la provenienza di ciascun nodo e il contenitore dei `bitvectors`. I risultati finali sono gestiti tramite gli array ausiliari `v` e `leaves`, entrambi dipendenti dal numero delle foglie . Incolonnando l'esecuzione con questa struttura dati orizzontale, QuickScorer ribalta le prestazioni: assicura un basso tasso di branch misprediction (Low branch misprediction rate) e massimizza l'efficienza della memoria con un alto cache hit ratio.

+4

[INSERIRE IMMAGINE: Schema che rappresenta l'architettura lineare degli array di QuickScorer: offsets che puntano alle sezioni dell'array thresholds (ordinato in modo crescente), seguiti dagli array tree_ids, bitvectors, v e leaves]

### Valutazione e Risultati Sperimentali

L'efficacia pratica di questa architettura è stata dimostrata tramite approfonditi test comparativi che misurano il tempo di scoring per singolo documento, espresso in microsecondi, e il relativo fattore di accelerazione (speedup). I test sono stati condotti utilizzando dataset di riferimento del settore come **MSN-1** e **Y!S1**. L'esperimento ha valutato foreste di densità crescente, testando insiemi di 1.000, 5.000, 10.000 e fino a 20.000 alberi decisionali. Nelle batterie di prova, sono stati confrontati fianco a fianco quattro metodi: **QS** (QuickScorer), **VPRED**, **IF-THEN-ELSE**, e **STRUCT+**. I modelli sono stati inoltre declinati in base al numero di foglie massime per albero: 8, 16, 32 e 64.

+1

Di seguito una tabella sintetica che ritrae una porzione dei risultati su modelli da 1.000 e 5.000 alberi con foglie di livello 8 (i dati completi si estendono parallelamente per tutte le configurazioni analizzate):

| Metodo           | Alberi: 1.000 (MSN-1) | Alberi: 1.000 (Y!S1) | Alberi: 5.000 (MSN-1) | Alberi: 5.000 (Y!S1) |
| ---------------- | --------------------- | -------------------- | --------------------- | -------------------- |
| **QS**           | 2.2 (-)               | 4.3 (-)              | 10.5 (-)              | 14.3 (-)             |
| **VPRED**        | 7.9 (3.6x)            | 8.5 (2.0x)           | 40.2 (3.8x)           | 41.6 (2.9x)          |
| **IF-THEN-ELSE** | 8.2 (3.7x)            | 10.3 (2.4x)          | 81.0 (7.7x)           | 85.8 (6.0x)          |
| **STRUCT+**      | 21.2 (9.6x)           | 23.1 (5.4x)          | 107.7 (10.3x)         | 112.6 (7.9x)         |

I dati confermano che QuickScorer sovraperforma nettamente i metodi precedenti, offrendo speedup enormi rispetto alla naive baseline If-then-else e surclassando anche le tecniche più avanzate come Struct+ e VPred in ogni ordine di scala.

---

### Glossario e Concetti Chiave

- **Branch Misprediction**: L'errore che commette il processore del computer quando tenta di indovinare in anticipo la destinazione di un'istruzione condizionale (come un `if`). Nelle foreste decisionali tradizionali, l'imprevedibilità dei rami causa continui "vuoti" computazionali e rallentamenti.

- **QuickScorer**: Innovativo algoritmo di inferenza che abbandona l'esplorazione gerarchica degli alberi, convertendo la struttura di decisione in array lineari ordinati ed eseguendo calcoli matematici logici al posto dei salti condizionali.

- **Bitvector / Maschere di bit**: Insiemi di bit associati a ciascun nodo dell'albero in QuickScorer. Attraverso operazioni di AND logico basate esclusivamente sui nodi di cui non si è verificata la condizione ("False Nodes"), il sistema riesce a isolare direttamente l'indice della foglia risolutiva per assegnare il punteggio finale.

- **Interleaved Tree Traversals**: La logica strutturale di QuickScorer che consiste nell'impacchettare i dati di tutti gli alberi per raggrupparli in sequenza per "feature" e ordinarli. Questo permette al processore di leggere e valutare i dati della memoria cache in un flusso continuo e altamente efficiente.

---

# Slide 10:# Modelli Linguistici

In questa prima sezione dedicata all'elaborazione del linguaggio naturale, esploreremo le fondamenta dei modelli computazionali usati per la rappresentazione dei testi, partendo dagli approcci stocastici per arrivare alle moderne reti neurali.

### Modelli Probabilistici e Modello Statistico del Linguaggio

L'indagine parte dall'analisi dei **modelli probabilistici**. Un **modello statistico del linguaggio** può essere definito, nella sua essenza, come una distribuzione di probabilità $P$ che viene applicata a specifiche sequenze di termini. Immaginiamo di analizzare un documento $d$ che è composto da una precisa sequenza di parole, come ad esempio $w_{1}w_{2}w_{3}$. Tramite l'applicazione delle leggi della probabilità condizionata, possiamo calcolare la probabilità dell'intero documento tramite la seguente espressione: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2})$. Sulla base delle diverse assunzioni che decidiamo di fare su questa distribuzione di probabilità, abbiamo la possibilità di creare modelli statistici che presentano differenti gradi di complessità. La formula appena citata è, a livello teorico, perfetta: essa non fa alcuna assunzione pregressa ed è quindi capace di modellare in modo esatto le caratteristiche di qualsiasi lingua. Tuttavia, si rivela totalmente impraticabile all'atto pratico, dal momento che per funzionare richiederebbe di imparare le probabilità di letteralmente qualsiasi sequenza di parole che possa esistere nel linguaggio considerato.

### Modello a Unigrammi e Modelli N-gram

Per superare i limiti computazionali del modello generale, occorre introdurre delle assunzioni semplificative. Il **modello a unigrammi** assume che esista un'assoluta indipendenza statistica tra le varie parole di un testo. In base a questa regola, la probabilità che un documento $d$ esista è semplicemente data dal prodotto matematico delle probabilità singole delle sue parole: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2}) = P(w_{1})P(w_{2})P(w_{3})=\prod_{i}P(w_{i})$. Proprio per via di questa forte e irrealistica assunzione di indipendenza, il classificatore bayesiano che sfrutta tale logica viene denominato "naïve" (ingenuo). A livello ingegneristico, questi modelli utilizzano comunemente i logaritmi delle probabilità per poter operare in modo più agevole all'interno di uno spazio lineare: $\log(\prod_{i}P(w_{i}))=\sum_{i}\log(P(w_{i}))$. Bisogna però considerare il problema dei termini rari o mancanti; per ovviare al rischio che il sistema generi probabilità pari a zero annullando il prodotto complessivo, si adotta una tecnica nota come **smoothing**, che solitamente consiste nell'aggiungere artificialmente un'unità ("add one") a tutte le frequenze calcolate.

Un approccio capace di introdurre maggiore ricchezza informativa è il **modello a bigrammi**, o più genericamente il **modello n-gram**. A differenza dell'unigramma, il bigramma assume che vi sia una dipendenza statistica tra una data parola e quella che la precede immediatamente. Di conseguenza, la formula si sviluppa come segue: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2}) = P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{2})=\prod_{i}P(w_{i}|w_{i-1})$. Sebbene sembri una modifica banale, questa semplice aggiunta rende il modello già ampiamente in grado di catturare una buona quantità di regolarità intrinseche del linguaggio. Applicando questo concetto su scale più ampie, emerge un evidente compromesso matematico (trade-off): maggiore è la lunghezza dell'n-gram che adottiamo nel nostro modello, maggiore è la quantità di semantica che viene catturata. D'altra parte, però, all'aumentare della lunghezza della stringa si abbassa la significatività statistica del modello stesso, sbilanciandolo verso un comportamento orientato alla pura memorizzazione a scapito della capacità di generalizzazione.

### Modelli Neurali e l'Introduzione di Word2Vec

Il vero punto di rottura rispetto alla tradizione si ottiene con il passaggio ai **modelli neurali**. Lo standard di riferimento in questo ambito è **Word2Vec**. I due principali approcci proposti dall'architettura di Word2Vec, noti come **Skip-gram** e **CBoW** (Continuous Bag-of-Words), definiscono due specifici task speculari: il primo si focalizza sulla predizione di un contesto a partire da una parola nota, mentre il secondo si occupa di predire una parola specifica basandosi sul suo contesto di contorno. Da un punto di vista strutturale, entrambi i meccanismi vengono implementati come una rete neurale lineare dotata di due soli livelli ("two-layers linear neural network"). All'interno di queste reti, sia le parole immesse in input che quelle attese in output sono fornite sotto forma di rappresentazioni di tipo "one-hot". Tali vettori sparsi vengono quindi prima codificati e successivamente decodificati verso e da una **rappresentazione densa** caratterizzata da una dimensionalità molto più contenuta.

[INSERIRE IMMAGINE: Diagrammi a blocchi dei modelli Skip-gram e CBoW. Nel modello Skip-gram, la parola di input $w_t$ viene elaborata dal livello nascosto $h$ per generare le proiezioni sulle parole di contesto $w_{t-2}, w_{t-1}, w_{t+1}, w_{t+2}$. Nel modello CBoW, le parole di contesto $w_{t-2}, w_{t-1}, w_{t+1}, w_{t+2}$ convergono verso il livello nascosto $h$ per poter effettuare la predizione della parola centrale $w_t$.]

### L'Apprendimento degli Embeddings e il Contesto

Risulta fondamentale comprendere che gli **embeddings** (cioè i vettori d'incorporamento) costituiscono, di fatto, un semplice sottoprodotto ("byproduct") del task di predizione delle parole. Anche se le reti affrontano un evidente task di previsione, esse presentano il formidabile vantaggio di poter essere addestrate virtualmente su qualsiasi blocco di testo. Questo processo avviene in modo completamente non supervisionato, rimuovendo la necessità di impiegare lunghi e costosi dati etichettati dall'uomo ("human-labeled data").

[INSERIRE IMMAGINE: Cattura dallo show televisivo "L'Eredità" in cui, attraverso il gioco del collegamento semantico, le parole indizio TITOLO, ESEMPIO, DOTTORE, SENZA, CONSERVARE e PIENO guidano alla predizione della parola mancante, evidenziando le dinamiche umane per le associazioni contestuali di termini. Il premio in palio mostrato è di 80.000 e si nota un errore ortografico nella parola ENERGIA barrata.]

Per determinare quali parole facciano parte dell'orizzonte di analisi, si definisce la dimensione di una finestra di contesto ("context window size"). Tale parametro solitamente spazia in un range compreso tra due e cinque parole prima e dopo il termine centrale di riferimento. È interessante notare che adottare finestre di ampiezza maggiore (tipicamente tra i 6 e i 10 lemmi) consente di catturare una più ricca rete semantica, riducendo di conseguenza il peso delle sole relazioni sintattiche locali. Al termine del training, la dimensione tipica dei vettori di embedding risultanti in Word2Vec si assesta usualmente in un valore compreso tra le 200 e le 300 dimensioni.

### Funzionamento Interno dei Livelli di Word2Vec

La struttura della rete Word2Vec si basa sull'interazione di due specifici livelli funzionali. Il primo di questi strati prende in input la matrice $W_I$, che corrisponde alla rappresentazione altamente sparsa delle parole (ovvero il vettore "one-hot" avente una dimensione pari a $|F|$) e ha lo scopo di generare una nuova rappresentazione astratta, denotata come $h$, che incapsula le informazioni del contesto. Successivamente interviene il secondo strato, governato dalla matrice dei pesi $W_o$. Questo livello ha l'incarico di prendere l'astrazione vettoriale $h$ e convertirla in un vettore finale di probabilità $u$, il quale rappresenta l'effettiva predizione del target linguistico ricercato. Infine, tale output viene normalizzato ricorrendo alla nota funzione softmax.

[INSERIRE IMMAGINE: Architettura dettagliata a due livelli di Word2Vec, che illustra il flusso informativo dove le parole del contesto (rappresentate da input come $w_{t-2}$, $w_{t-1}$, $w_{t+1}$, $w_{t+2}$) vengono moltiplicate per la matrice di input $W_I$ per creare l'astrazione nascosta $h$. Questa passa poi attraverso la matrice d'uscita $W_O$ creando la predizione $u$, applicando poi softmax per ottenere la parola predetta $w_t$.]

L'aspetto teoricamente più incisivo di tutta questa architettura si verifica proprio nel primo strato di pesi della rete neurale: difatti, ogni singola riga della matrice $W_I$ costituisce esattamente l'embedding reale del termine. Questo valore vettoriale viene raffinato e calibrato in maniera incrementale lungo l'intero processo di addestramento legato al task della predizione contestuale. L'implicazione di questo addestramento iterativo è che quelle parole che condividono un uso analogo o contesti simili nel linguaggio naturale, finiranno inesorabilmente per essere collocate in posizioni affini nello spazio, assumendo quindi vettori di embedding tra loro molto simili. Straordinariamente, vari e articolati aspetti concettuali e relazionali del linguaggio si ritrovano modellati implicitamente nelle diverse dimensioni matematiche che costituiscono questi vettori densi.

---

### Concetti Chiave

- **Modello Statistico del Linguaggio**: Distribuzione di probabilità che modella in forma quantitativa sequenze di termini e parole per predirne la corretta formulazione testuale.

- **Smoothing**: Processo correttivo (ad esempio l'aggiunta di "1" a ogni frequenza contata) essenziale per impedire che i termini a probabilità zero inficino i calcoli a catena dei modelli probabilistici.

- **Modelli N-gram**: Astrazioni statistiche in cui la occorrenza di un termine non è indipendente, ma condizionata da $n-1$ termini immediatamente precedenti nella sequenza grammaticale.

- **Word2Vec**: Modello neurale lineare impiegato per derivare vettori rappresentativi densi (embeddings) del testo, operando attraverso i task non supervisionati di Skip-gram e CBoW.

- **Embedding**: Rappresentazione vettoriale densa di limitate dimensioni, ottimizzata per riflettere le vicinanze d'uso di un termine, la cui estrazione avviene non come target ma come prezioso sottoprodotto del training neurale.

---

### Il Test degli Embeddings e l'Impatto dei Dati di Addestramento

Per valutare l'efficacia delle rappresentazioni vettoriali, è fondamentale sottoporle a specifici test volti a verificare se gli embeddings siano effettivamente in grado di catturare le corrette proprietà sintattiche e semantiche del linguaggio.

[INSERIRE IMMAGINE: Diagrammi vettoriali che illustrano come la distanza e la direzione tra le parole 'MAN' e 'WOMAN' siano parallele a quelle tra 'UNCLE' e 'AUNT', o tra 'KING' e 'QUEEN', dimostrando la capacità di catturare il genere. Un secondo schema mostra il parallelismo tra 'KING' e 'KINGS' rispetto a 'QUEEN' e 'QUEENS', evidenziando la cattura della pluralità.]

Una delle metodologie più celebri è il test delle analogie. Questo test si basa su relazioni proporzionali e semantiche: ad esempio, ci si chiede quale concetto stia a Roma così come Parigi sta alla Francia ("Paris stands to France as Rome stands to ?"). Altri esempi includono "Writer stands to book as painter stands to ?" oppure "Cat stands to cats as mouse stands to ?". Matematicamente, questa complessa astrazione linguistica si risolve sorprendentemente attraverso basilari operazioni algebriche sui vettori; l'analogia geografica appena menzionata può essere approssimata con la formula matematica $e(\text{France}) - e(\text{Paris}) + e(\text{Rome}) \approx e(\text{Italy})$. Più in generale, data un'analogia del tipo $a : b = c : d$, il vettore incognito $d$ si calcola cercando nello spazio vettoriale il termine $x$ che massimizza il prodotto e la normalizzazione tra le distanze, con la formula $d = \arg\max_x \frac{(e(b) - e(a) + e(c))^T e(x)}{||e(b) - e(a) + e(c)||}$.

Tuttavia, nell'analisi e nell'impiego dei vettori bisogna sempre tenere a mente l'importanza cruciale del contesto di origine. La specifica fonte (il dataset) sui cui un modello viene addestrato determina inevitabilmente quale specifica sfumatura semantica verrà catturata e prioritizzata. A dimostrazione di ciò, si può osservare come l'addestramento svolto su Wikipedia produca associazioni concettuali drasticamente diverse per alcuni termini ambigui rispetto a un addestramento condotto su un corpus testuale basato sui libri.

| **Termine Analizzato** | **Prossimità su Modello WIKI** | **Prossimità su Modello BOOKS** | **Prossimità su Modello WIKI** | **Prossimità su Modello BOOKS** |
| ---------------------- | ------------------------------ | ------------------------------- | ------------------------------ | ------------------------------- |
| sega / chianti         | dreamcast                      | motosega                        | radda                          | merlot                          |
| sega / chianti         | genesis                        | seghe                           | gaiole                         | lambrusco                       |
| sega / chianti         | megadrive                      | seghetto                        | montespertoli                  | grignolino                      |
| sega / chianti         | snes                           | trapano                         | carmignano                     | sangiovese                      |
| sega / chianti         | nintendo                       | smerigliatrice                  | greve                          | vermentino                      |
| sega / chianti         | sonic                          | segare                          | castellina                     | sauvignon                       |
|                        |                                |                                 |                                |                                 |

### Implementazione Pratica, FastText e le Parole OOV

A livello di pratica ingegneristica, per il calcolo e la gestione computazionale degli embeddings, la nota libreria Python *Gensim* fornisce un'implementazione estremamente efficiente e ricca di dettagli utili. In termini di codice, l'addestramento è diretto: è sufficiente definire un input strutturato di frasi (`sentences = [['this', 'is', 'a', 'sentence'], ['this', 'is', 'another', 'sentence']]`), importare il modulo (`from gensim.models import Word2Vec`) e istanziare l'oggetto passandogli le frasi (`model = Word2Vec(sentences)`). Ovviamente, per finalità didattiche e di ricerca, esistono implementazioni di Word2Vec skip-gram scritte da zero utilizzando framework di alto livello come PyTorch.

Un notevole passo evolutivo nella tecnica di rappresentazione vettoriale è stato introdotto da **FastText**. Questo approccio estende le logiche del classico modello di embedding di Word2Vec scendendo a un livello di granularità sub-lessicale, ovvero lavorando integrando gli n-grammi che compongono le singole parole. In FastText, una parola come "goodbye" non è più trattata come un singolo token indivisibile, ma viene esplosa ed è rappresentata anche attraverso il suo corredo di n-grammi, ai quali si sommano i delimitatori speciali '<' e '>' indicanti l'inizio e la fine del lemma. La parola si frammenta quindi in sequenze quali "<go", "goo", "ood", "odb", "dby", "bye" e "ye>". La lunghezza esatta dell'n-gramma da generare è un iperparametro liberamente configurabile, ma nella pratica comune la finestra tipica impone di includere tutti gli n-grammi aventi una lunghezza che varia dai 3 ai 6 caratteri.

Alla luce di questa scomposizione, l'embedding definitivo di una parola viene calcolato dinamicamente eseguendo la somma vettoriale tra l'embedding standard della parola intera e l'embedding collettivo dei suoi frammenti (n-grammi) costitutivi. Sfruttare queste "subword information" garantisce tre colossali vantaggi sul campo. In primo luogo, dona al sistema la capacità inestimabile di calcolare e assegnare un embedding sensato anche a quelle parole **OOV** (Out Of Vocabulary), ovvero termini mai apparsi nel dataset di training originale. In secondo luogo, migliora sensibilmente la qualità della mappatura per tutte le parole afflitte da refusi o errori ortografici accidentali (misspelled words). Infine, a dimostrazione della sua scalabilità, FastText distribuisce oggi vettori preaddestrati su enormi corpus per oltre 200 lingue differenti. A titolo dimostrativo, lanciando una query sul termine "gearshift", il modello propone immediatamente affinità logiche strettissime (score prossimi a 0.77-0.79) con concetti derivati come gearing, flywheels, flywheel, gears, driveshafts, e varianti come driveshaft, daisywheel, wheelsets, epicycles e gearboxes. Allo stesso modo, fornendogli una parola scritta clamorosamente male come "accomodation" (mancante di una 'm'), la struttura a n-grammi colma l'errore associandovi prontamente variazioni come accomodations, l'esatto accommodation, o termini tematici robusti come accommodations, accommodative, accomodating, amenities, hostelling, catering, greenbelts o hospitality.

### Dai Vettori di Parole alle Rappresentazioni di Documenti (Doc2Vec)

Avanzando di un livello di astrazione, il problema successivo risiede nel comprendere come rappresentare non solo lemmi discreti, ma documenti completi all'interno di uno spazio continuo di embedding. Poiché, nella sua forma più elementare, un documento non è altro che un insieme ("set") di parole contenute al suo interno, la logica più elementare suggerisce di prendere i vettori densi di queste singole parole e combinarli assieme. Tramite operazioni statistiche come il calcolo della media ("average") oppure l'estrazione del valore massimo tra essi ("max pooling"), diviene possibile produrre un unico vettore riassuntivo che si fa carico dell'embedding del documento intero.

Una strada più sofisticata e progettata *ad hoc* consiste nell'estendere l'architettura originaria di Word2Vec per modellarvi in forma esplicita i documenti, dando vita al modello noto come **Doc2Vec**. Proposto originariamente dai ricercatori Le e Mikolov, Doc2Vec altera il paradigma di Word2Vec integrando dimensioni di input aggiuntive destinate in via esclusiva a recepire gli identificatori di sistema unici legati ai documenti (identifiers of documents). Di riflesso a questa mutazione architetturale, la matrice di input $W_I$ assume una nuova dimensione di $(|D| + |F|) \cdot |h|$. Questo permette agli identificatori (Document ids) di essere mappati e proiettati esattamente nello stesso iperspazio astratto abitato fino a quel momento solo dalle parole. Sfruttando un modello addestrato in questa forma, è possibile procedere anche con l'inferenza di vettori per documenti totalmente nuovi e mai visti prima; sarà sufficiente passare alla rete le parole che compongono il nuovo documento e lasciare che il sistema vi posizioni lo spazio semantico.

[INSERIRE IMMAGINE: Schema architetturale a rete neurale del sistema Doc2Vec. Insieme agli input di contesto temporale come $W_{t-2}$, $W_{t-1}$, $W_{t+1}$ e $W_{t+2}$, vi è un elemento di input distinto chiamato "docId" proiettato in una matrice $W_I$, i cui segnali convergono nel nodo nascosto $h$ e fuoriescono da $W_O$ verso la predizione finale filtrata da softmax.]

In un approccio più "moderno" e svincolato, possiamo anche saltare le combinazioni matematiche a priori, e utilizzare direttamente e grezzamente gli embeddings delle singole parole come se fossero il primissimo strato di una più complessa rete neurale profonda, delegando interamente alla capacità esplorativa della rete neurale il gravoso compito di imparare e modellare implicitamente la totalità del contenuto semantico del documento lungo i suoi livelli convoluzionali o ricorrenti.

### Gli Embeddings nell'Ecosistema delle Reti Neurali e il Padding

Approfondendo l'aspetto implementativo all'interno del Deep Learning, il livello deputato all'embedding costituisce, di prassi, il primo strato operativo in assoluto di un'architettura neurale votata all'NLP. Strutturalmente si tratta di un semplice strato lineare fondato su una grande matrice di pesi $W$ la cui dimensione è $|F| [cite_start]\cdot n$, dove il parametro $n$ fissa brutalmente e deterministicamente la dimensione desiderata per lo spazio vettoriale (la lunghezza del vettore embedding per parola). Come accennato, tale matrice si assume l'incarico vitale di tradurre entità linguistiche isolate nelle celebri e lavorabili rappresentazioni dense (dense representations). La natura versatile di questo livello permette di inizializzarne i valori assegnando ai pesi vettori puramente casuali che verranno affinati in seguito, oppure sfruttando le solide basi offerte da vettori preaddestrati. Proprio in riferimento all'addestramento, qualora si scelgano pesi preaddestrati d'alta qualità, lo sviluppatore ha facoltà di "congelarli" mantenendoli fissi per tutta la sessione di addestramento, oppure di sbloccarli e aggiornarli ("updated"), avviando di fatto un processo di adattamento e fine-tuning dell'embedding stesso che gli permetterà di performare al meglio sullo specifico task in elaborazione.

[INSERIRE IMMAGINE: Diagramma di flusso operativo dell'incorporamento nelle reti neurali. Un testo descrittivo testuale ("all work and no play...") entra come stringa e viene tokenizzato in una 'Sequenza di word ids' (es. [2,4,1,8,10,5,0,0...]). Attraversando un blocco centrale chiamato 'Embeddings', in cui convergono i valori preaddestrati, i numeri divengono una massiccia 'Sequenza di vettori di embedding', che alla fine del flusso si dirama alimentando i vari blocchi convoluzionali (Convolutional) o ricorrenti (Recurrent).]

Per analizzare il diagramma esposto in precedenza con un caso d'uso: prendendo un frammento testuale grezzo ("Text") come "all work and no play...", la macchina elabora la sequenza di identificatori, che si configura in una lista intera, ad esempio [2,4,1,8,10,5,0,0,0,0,0,0]. Tale sequenza si infrange sul layer degli Embeddings (guidato dai "Pretrained values" se presenti) scaturendo in uscita un raggruppamento o sequenza di vettori, matrici multidimensionali che racchiudono file come `[0.2, -0.3, 0.9, -0.2... 0.8]`, `[-0.1, 0.7, 0.7, -0.1... -0.1]` e così via. Tutto questo è facilmente toccabile con mano esplorando i branch dei repository GitHub ufficiali di framework come Keras (es. `imdb_cnn.py`), dove viene palesato l'interfacciamento tra livelli linguistici e strati puramente computazionali Convolutional o Recurrent.

Trattare dati in serie comporta però due insidie per l'architettura neurale, per le quali occorrono degli accorgimenti drastici: le OOV words e la necessità imperativa di allineamento dimensionale nel padding. Riguardo al primo problema, poiché i modelli linguistici (LMs) che si basano rigidamente su un vocabolario testuale fisso non sono capaci, per loro stessa natura progettuale, di gestire e modellare parole "Out Of Vocabulary", occorre aggirare l'ostacolo addizionando preventivamente allo spazio un elemento speciale artificiale noto come parola sconosciuta o "unknown word". Ovviamente, per poterla inglobare nell'architettura, la si correda subito del suo embedding isolato da far imparare progressivamente al sistema lungo le epoche di addestramento. Per via del loro funzionamento intrinseco volto alla parallelizzazione computazionale da affidare alle schede grafiche, le NNs tendono a non processare quasi mai frasi singole alla volta, preferendo raggruppare i campioni in un lotto unitario definito "batch" contenente $k$ esempi paralleli. Questo porta al secondo inghippo meccanico: per processare i flussi simultaneamente è strettamente tassativo che le sequenze fornite nel blocco posseggano una lunghezza spaccatamente identica l'una con l'altra. La tecnica per forzare questa uniformità richiede di prendere la frase di calibro maggiore nel lotto e di allungare artificialmente tutte le restanti. Questo avviene inserendo una speciale "**padding** word" (la parola riempitiva neutrale) per allungare le sentenze e fare perno (match) con la lunghezza massima. Anche tale token è affiancato da un suo micro-vettore embedding inattivo e l'inserimento non è arbitrario: la prassi impone d'essere puramente e inflessibilmente coerenti riguardo al posizionamento spaziale del padding, accodandolo o premettendo alle parole sempre e solo da un solo lato (before/after) in ogni computazione per impedire che l'ordine sequenziale causi danni nel processamento.

### L'Evoluzione verso la Self-Attention e i Transformer

Allargando l'orizzonte verso le attuali frontiere del machine learning si nota chiaramente che negli ultimissimi anni stiamo assistendo alla genesi di una nuova generazione tecnologica con schiere di modelli profondi concorrenti e ferocemente competitivi in campo linguistico, dotati di skill modellistiche linguistiche e di generazione testuale classificate quasi unanimente come "outstanding" e impressionanti.

[INSERIRE IMMAGINE: Infografica a loghi sovrapposti che simboleggia l'ascesa delle grandi reti. Mostra gli emblemi fiammeggianti e i nomi in rassegna dei titani tecnologici del settore come THE TRANSFORMER, ULM-FIT, OpenAI Transformer, ELMO e BERT.]

L'eccezionalità di queste performance non proviene dal nulla, bensì è radicata nel massiccio sforzo ingegneristico per architetture neurali di scala sbalorditiva. Il mercato è popolato da reti bi-direzionali applicate in profondità a livello di singolo carattere tramite celle LSTM – delle quali il capostipite fu indubbiamente ELMO – affiancate a logiche mostruosamente parallele come la tanto decantata **multi-headed self-attention** che troneggia sulle fondamenta di sistemi leader come The Transformer, lo scibile dietro le OpenAI Transformer, le reti BERT e una miriade di loro cloni. Mentre le versioni arcaiche di vettori inglobavano semantica basica su testi localizzati, queste corazzate divorano enormi ed estese collezioni mondiali di documenti, immagazzinando una conoscenza tentacolare che sbilancia il peso degli addestramenti fino a inghiottire sistemi dotati di molteplici miliardi di parametri ("billions of parameters") computazionali liberi e modificabili.

Al cuore pulsante di tutto quest'ecosistema troneggia fiera l'operazione nota come **Self-attention**. Si tratta non di un semplice escamotage sintattico ma di una profondissima tecnica neurale il cui scopo primario e unico è imparare con minuziosità chirurgica il contributo esatto e preciso di ogni elemento contestuale del testo verso la definizione finale di quella che diventerà l'autentica semantica in quel momento applicata alla singola parola, tenendo costantemente un faro vigile sul qui-e-ora della frase e distogliendosi dal generico preaddestramento fisso. Questa meraviglia emerge chiaramente monitorando con visori il lavoro in parallelo delle numerose "teste" (Head). In questa scomposizione e delegazione del lavoro, analizzando una singola passata su un input come "found in taiwan. [SEP] the wingspan is 24 - 28 mm." si scorgono comportamenti differenti e mirati da parte delle teste che coabitano il network. Alcune teste come la "Head 1-1" focalizzano una vista "a scorrimento largo", guardando in forma sparsa e dilatata un po' a ogni parola per scovarne il senso e i macro-topic, lavorando su associazioni molto estese e broadly attendute. Parallelamente, una "Head 3-1" adotta un'intelligenza decisamente da n-gramma stretto, appuntando le sue frecce sintattiche prettamente in avanti ad anticipare il singolo token successivo nell'attesa e nel "next token prediction". Poi giungono sul palco intelligenze altamente asettiche e regolamentate dal programmatore: abbiamo teste puntiformi quali la "Head 8-7" con la mania di non guardare i termini, se non l'unico elemento di delimitazione contestuale netto [SEP] al termine del segmento ; similmente si muove la "Head 11-6" in piena ricerca esclusiva sul tracciamento ostinato di ogni punto fermo o dei caratteri di stop per captare l'andamento frasale. L'ammontare quantitativo (o oscurità delle connessioni visibili, la "darkness of a line" che indica la palese intensità della weight attenzionale assorbita ) ci dimostra plasticamente come i cervelli profondi, smontando un segmento di frase operino del tutto similmente all'addestramento su una fotografia. Questo esatto processo modulare somiglia vertiginosamente al funzionamento su grid dei progressivi filtri classici delle CNN (Convolutional Neural Networks) nel loro apprendimento visuale, in cui le prime linee catturano contorni basilari al 'Layer 1', per poi progredire e stratificarsi su strutture semantiche complesse attraversando i Layer 2 e 3 del reticolato.

[INSERIRE IMMAGINE: Diagrammi a linea mostrano concretamente i vari "pattern" attenzionali, in cui fili viola molto marcati uniscono a colpo d'occhio termini fra le due colonne parallele del testo frasale. A fianco è disposta l'affinità visiva alle immagini generate e destrutturate dei filtri convoluzionali impilate sulle classiche layer-grid delle CNN, dalla 1 alla 3.]

### Rappresentazioni Dense e Operazioni di Information Retrieval

Un'applicazione potentissima in cui vettori e modelli attenzionali danno frutti diretti tangibili al ricercatore risiede ovviamente nelle operazioni veloci di *Retrieval* su dataset spropositati, incrociando i flussi asincroni di architetture neurali Siamese. In quest'ottica si sdoppia l'azione: tutto ha inizio con il duro lavoro in background di elaborazione della sorgente (la cosiddetta "Offline Computation") durante la quale si inietta l'ennesimo lungo documento denotato come documento $d$, costituito per convenzione da un numero $m$ di discreti token, all'interno della trafila fitta e chiusa della Rete Neurale e dei suoi meccanismi interni. Ne uscirà a fatica un compatto Feature Vector di $d$ a disposizione del db. Sull'altra parallela sponda, magari ad accensione server già partita e a runtime reattivo, scaturirà improvvisa una *query* indicata formalmente con la lettera $q$ di una precaria e minuscola lunghezza stimata in $n$ token. Attraversato il suo network identico al documento otterrà simmetricamente un istantaneo *Feature Vector di q* in uscita. Mettendo faccia a faccia l'un l'altro i vettori nella nuova *Dense Representation*, scatta il calcolo dell'affinità (lanciando un classico prodotto scalare – dot product) che materializzerà sotto forma tangibile e utile per i ranking uno specifico e prelibatissimo calcolo matematico: il punteggio di rilevanza $S_{q,d}$ tra lo stimolo umano $q$ e la risposta documentale $d$.

Le architetture neurali, forti dei loro posizionamenti e misurazioni della prossimità tramite dot product o per similarità o coseno angolare (cosine similarity) puntano massimamente tutto il peso sulla tattica topologica che va sotto l'arcinoto acronimo del **k Nearest Neighbour (KNN) Retrieval**. Lo scopo della ricerca in questo reame si fa estremamente pragmatico: trovare materialmente fra l'universo le associazioni iperlocali identificando i documenti in modo spietato trovando esclusivamente i "closest $k$ points" (i punti con maggiore densità vettoriale) sparsi e confinati all'interno del proprio gigantesco reame iperdimensionale N-dimensionale. Soffermandoci sulla performance in scala massiccia ("Retrieval as finding"), la ricerca sviscerata del risultato Esatto e minuzioso è costosa ("All d's against q"): scontrar ogni punto richiede pesantissime comparazioni asintotiche computate matematicamente sui logaritmi in notazione "O grande" come tempistiche piene a scalino pari a $O(n \log k)$ e appoggiandosi incessantemente a strutture dati ingorde quali gli *Heap* programmati all'esplicito e solo fine di trattenere nella vetta delle code solo gli ammessi $k$ elementi fregiati della più elevata similarità misurabile fra i vettori.

Data questa strozzatura cronica asfissiante sui Big Data, i ricercatori dibattono sul noto dualismo "Slow/Exact vs Fast/Approximate", prediligendo massicciamente le procedure di recupero rapide mediante stratagemmi euristici ed imperfetti, racchiusi storicamente nella famiglia di tecnologie **ANN** (Approximate Nearest Neighbour). Le variegate metodiche spaziano infatti in svariate ramificazioni architetturali di stivamento veloce tra cui si ramificano con forza sistemi astuti a partizionamento che utilizzano ad albero binario (Trees), connettività iperdimensionali spaziali tramite grafi e link direzionali (Graphs), funzioni hash locazionali tolleranti ai margini (LSH) o durissime quantizzazioni numeriche compattanti.

[INSERIRE IMMAGINE: Scatter plot su sfondo chiaro popolato da densissime nubi di punti rossi, in cui spicca solitaria in una sponda del piano un punto blu, bollato chiaramente come query vettoriale $q$. Da essa irradiano a ventaglio quattro inequivocabili frecce nere indirizzate rigidamente sui quattro vicini rossi in assoluto dotati di minima distanza geometrica nello spazio bidimensionale KNN mostrato.]

### Conclusioni Fondamentali sui Modelli Linguistici

Terminando il percorso attraverso le stratificazioni di astrazione e l'implementazione pratica ingegneristica, dobbiamo riconoscere che i Language models si ergono oggi incontestabilmente come uno dei più potenti, scalabili e flessibili strumenti computazionali in circolazione nel panorama delle tecnologie del software per collezionare massivamente informazioni estrapolate, in forma e procedura totalmente "unsupervised", partendo nativamente da qualsiasi fonte semantica complessa o da uno specifico dominio testuale target per poi riversare il tutto e condensarlo magicamente in strabilianti forme rappresentative latenti e intrinsecamente "machine-readable".

Le vertiginose proprietà derivate dalla trasformazione in tali costrutti di forma vettoriale garantiscono al giorno d'oggi ai data scientist due imponenti e cruciali rampe di lancio finalizzate all'indagine applicativa. In primis, esse rendono immediatamente e tecnicamente esplorabile la complessità di una lingua o i labirinti oscuri di un dominio, agevolando in modo stratosferico lo scovamento o "discover" semi-automatizzato delle innumerevoli entità sensibili rilevanti del settore affiancate alla decodifica istantanea del loro groviglio incomprensibile di sotterranee interrelazioni, vincoli sintattici strutturali e affinità semantiche dirette. In secundis e in forma più pragmatica, i modelli incamerano una mostruosa spugna di saggezza linguistica ("infuse general knowledge") accumulata dalla lingua o dallo scibile umano e permettono successivamente a un banale e piccolissimo modello ad addestramento "supervised" di fruirne direttamente importandone il nucleo inietta al volo, portando la neonata e asettica rete a sprigionare abilità esecutive spaventosamente accelerate esprimendo sin dalle prime epoche una clamorosa abilità migliorativa in termini di potenza di astrazione trasversale e pura "generalization ability" finale.

---

### Concetti Chiave

- **FastText e OOV**: Evoluzione algoritmica applicata alla base dei sistemi di word embedding di concezione classica per esplorare le parole a livello sub-strutturale (n-grammi). Permette al sistema computazionale di superare il limite della parola ignota fuori dal dizionario addestrato (Out Of Vocabulary).

- **Doc2Vec**: Estensione formale per reti neurali sviluppata al fine di espandere le proprietà architetturali dell'algoritmo Word2Vec introducendo in input dei segnali identificativi o tag per mappare il senso logico testuale di un unico documento monolitico intero nei medesimi e collaudati spazi di vettorializzazione creati per accogliere singole parole discrete.

- **Self-attention**: L'ingegnosa tattica focale asincrona impiegata da Reti Transformer rivoluzionarie come BERT tramite blocchi denominati "Head". Il suo scopo principe è discernere e pesare indipendentemente ed ecletticamente il livello matematico e semantico delle influenze del micro e del macro contesto periferico frasale in cui giace avvolta una determinata parola osservata, prestando attenzioni mirate in direzione passata, futura o sui delimitatori frasali artificiali.

- **Approssimazione KNN (ANN)**: Assieme matematico di metodologie computazionali e architetturali in ambito Information Retrieval e vettoriale il cui intento unico è arginare le esplosioni o impennate nei rallentamenti algoritmici classici della tecnica Nearest Neighbour Esatta ($O(n \log k)$), operando calcoli con tolleranza di precisione sfruttando aggregati ad alberi informatici, cluster a grafo o hash geometrici di quantizzazione direzionale (LSH).

---

# Slide 11:# L'Utilizzo di BERT nell'Information Retrieval

**Introduzione** Il panorama dell'elaborazione del linguaggio naturale ha subito una trasformazione radicale a partire dal 2018, segnando l'inizio di una nuova era dominata dai modelli basati su architettura Transformer. In questa sezione esploreremo come i modelli linguistici avanzati, e in particolare BERT, siano stati adattati e integrati con successo nei sistemi di Information Retrieval (IR) per migliorare significativamente la pertinenza e la qualità delle ricerche, analizzando i metodi di calcolo offline e le strategie di interazione online.

### Il Punto di Svolta nel Natural Language Processing

L'anno 2018 ha rappresentato un vero e proprio punto di svolta per il Natural Language Processing (NLP). In questo periodo sono emersi modelli innovativi che hanno ridefinito lo stato dell'arte, come ad esempio **ULM-FiT**, **ELMo**, **OpenAI GPT** (definito anche come OpenAI Transformer) e, soprattutto, **BERT**. Al centro di questa rivoluzione vi è l'architettura denominata **The Transformer**, la quale ha fornito la base strutturale per questi potenti strumenti di comprensione linguistica.

### L'Architettura e i Fondamenti di BERT

L'acronimo **BERT** sta per *Bidirectional Encoder Representations from Transformers*. Si tratta di un modello linguistico che, fondamentalmente, definisce una distribuzione di probabilità su sequenze di parole. La sua caratteristica principale risiede nella capacità di catturare il contesto di una parola analizzando l'ambiente circostante in modo bidirezionale, guardando sia a sinistra che a destra del termine stesso. Questo modello apprende tramite due compiti principali: il **Masked Language Model** (MLM) e la **Next Sentence Prediction**.

Per gestire correttamente gli input, BERT impiega alcuni token speciali. Il token **[CLS]** è un token di classificazione utilizzato come operatore di pooling per ottenere un singolo vettore rappresentativo per l'intera sequenza testuale. Il token **[MASK]** viene impiegato specificamente nel task di Masked Language Model per indicare al modello la parola nascosta da predire. Infine, il token **[SEP]** viene usato per separare distintamente le diverse frasi fornite in input. A livello strutturale, l'input finale che il modello elabora (ad esempio per una stringa come "I love search engines") è dato dalla somma di tre componenti: i *Token Embeddings*, i *Segment Embeddings* e i *Position Embeddings*. Questo approccio innovativo è stato descritto in dettaglio nello studio di Delvin et al. al convegno NAACL-HLT del 2019.

[INSERIRE IMMAGINE: Diagramma dell'architettura dell'input di BERT che mostra la somma di Token Embeddings, Segment Embeddings e Position Embeddings per la frase "I love search engines"]

### Il Meccanismo di Self-Attention e Multi-Headed Attention

L'unità fondamentale su cui si basa l'architettura Transformer è il meccanismo di **Self-Attention**. Questa tecnica permette al modello di imparare autonomamente quali porzioni o frammenti di una sequenza testuale siano i più importanti per portare a termine un determinato compito.

[RIFERIMENTO VISIVO DEL PROFESSORE: L'immagine mostra come la parola "it" concentri la sua attenzione sul termine "animal" nella frase "The animal didn't cross the street because it was too tired"]

Come documentato nello studio "Attention Is All You Need" di Vaswani et al. (NeurIPS 2017), questo meccanismo calcola il contributo di ogni elemento contestuale per definire la semantica della parola analizzata. Per catturare sfumature diverse e relazioni contestuali multiple all'interno della stessa frase, il meccanismo di base viene replicato attraverso la **Multi-headed Attention**. In questo modo, diverse "teste" di attenzione (attention heads) operano in parallelo, permettendo al modello di mappare connessioni complesse tra le parole in modo simultaneo.

[INSERIRE IMMAGINE: Grafico della Multi-headed Attention che mostra molteplici linee colorate (le diverse attention heads) che collegano la parola "it" a varie altre parole della frase per catturare contesti differenti]

### L'Addestramento di BERT: Dal Pre-training al Fine-Tuning

Il processo di formazione di BERT si articola in due step fondamentali per adattarlo a compiti specifici (ad-hoc tasks). Il primo passo consiste nell'**Apprendimento Semi-supervisionato**. In questa fase, il modello viene addestrato su enormi quantità di testo grezzo, come ad esempio i dataset di Wikipedia o ampie raccolte di libri (Books). L'obiettivo principale qui è predirre la parola mascherata (language modeling), permettendo al modello di cogliere e interiorizzare i pattern profondi del linguaggio. Al termine di questo processo, BERT possiede solide capacità di elaborazione linguistica in grado di potenziare i modelli che verranno costruiti successivamente.

Il secondo passo è l'**Apprendimento Supervisionato**, ovvero il fine-tuning. In questa fase si prende il modello pre-addestrato e lo si allena su un compito specifico utilizzando un dataset etichettato. Ad esempio, per creare un classificatore di e-mail, il modello viene addestrato con messaggi classificati al 75% come Spam e al 25% come Non Spam. Fornendo in input messaggi come "Buy these pills" o "Win cash prizes", il modello impara a classificarli come Spam, mentre riconosce testi come "Dear Mr. Atreides, please find attached..." come messaggi legittimi (Not Spam).

### Approcci Neurali per l'Information Retrieval

L'applicazione delle reti neurali all'Information Retrieval si divide principalmente in due categorie metodologiche, a seconda di come query e documenti interagiscono.

La prima categoria comprende i **Metodi basati sulla Rappresentazione** (Representation-based methods). In questo paradigma, ogni documento e la query vengono rappresentati separatamente come **vettori densi** (dense vectors). Questo avviene attraverso una sequenza di calcoli neurali che, per ragioni di efficienza, vengono eseguiti offline per tutti i documenti (Offline Computation) e online solamente per la query in ingresso. Il modello utilizza un'architettura di tipo Siamese, dove le reti neurali usate per elaborare documento e query sono identiche. Il punteggio di rilevanza finale ($S_{q,d}$) si ottiene calcolando la similarità tra i due vettori rappresentativi, tipicamente attraverso un semplice prodotto scalare (dot product).

[INSERIRE IMMAGINE: Diagramma del flusso dei Metodi basati sulla Rappresentazione (Offline Computation), che mostra Documento e Query processati separatamente da Reti Neurali Siamesi per generare vettori densi prima del prodotto scalare]

La seconda categoria racchiude i **Metodi basati sull'Interazione** (Interaction-based methods). A differenza del metodo precedente, qui l'interazione avviene a un livello più granulare. La similarità a livello di singola parola o di termine tra una query e un documento viene esplorata direttamente online costruendo una **matrice di interazione**, basata sui vettori di embedding iniziali. Successivamente, questa complessa matrice viene data in pasto a un'ulteriore sequenza di calcolo neurale (una Neural Network addizionale) che si occupa di generare il punteggio di rilevanza finale per stilare il ranking.

[INSERIRE IMMAGINE: Diagramma del flusso dei Metodi basati sull'Interazione (Online Computation), illustrando la Query e il Documento che formano una Matrice di Interazione, passata poi a una Rete Neurale per il punteggio]

### L'Implementazione Pratica: MonoBERT

Un'implementazione diretta dei metodi di interazione è rappresentata da **MonoBERT**, basato sulla versione vanilla del modello. L'idea alla base di questo approccio è che la query e il documento vengano codificati congiuntamente in modo incrociato (jointly cross-encoded). Il formato di input fornito alla rete è strutturato unendo i testi: `[CLS] query [SEP] documento [SEP]`. In questo contesto congiunto, l'embedding contestuale associato al token iniziale `[CLS]` funge da punteggio di rilevanza aggregato per l'intera coppia query-documento, punteggio che costituisce l'output finale del modello e che viene utilizzato in maniera diretta per il ranking, come formalizzato dagli studi di Nogueira et al. nel 2019.

---

### Glossario dei Concetti Chiave

- **Bidirectional Context (Contesto Bidirezionale)**: La capacità del modello BERT di analizzare una parola considerando simultaneamente le parole che la precedono e quelle che la seguono, ottenendo una comprensione semantica profonda.

- **Self-Attention**: Il meccanismo interno ai Transformer che permette al modello di pesare dinamicamente l'importanza di ogni singola parola rispetto alle altre all'interno della medesima sequenza.

- **Architettura Siamese**: Una tipologia di rete neurale utilizzata nei metodi di rappresentazione, in cui due modelli identici e con gli stessi pesi processano separatamente la query e il documento per generarne i vettori densi.

- **Metodi Basati sull'Interazione**: Approccio in cui la relazione testuale viene valutata creando una matrice di interazione precoce tra i termini della query e quelli del documento, processata successivamente da un modello neurale.

- **MonoBERT**: Modello di ranking in cui query e documento vengono inseriti insieme nella rete in formato congiunto, sfruttando il token [CLS] per generare il punteggio di rilevanza della coppia.

---

# Metodologie Avanzate di Fine-Tuning e Rappresentazioni Dense nell'Information Retrieval

**Introduzione**

L'evoluzione dell'Information Retrieval ha subito una forte accelerazione grazie all'introduzione di modelli linguistici di grandi dimensioni. In questo capitolo, esploreremo in dettaglio come il modello BERT venga affinato per compiti specifici di ranking, per poi addentrarci nei metodi basati sulla rappresentazione densa dei documenti. Analizzeremo le metriche di apprendimento, le architetture di rete neurale adottate e le strategie di selezione dei dati per ottimizzare i risultati di ricerca.

### Il Fine-Tuning di MonoBERT per il Ranking

Come precedentemente introdotto, il modello base di BERT, noto anche come Vanilla BERT, viene pre-addestrato nativamente su due compiti fondamentali: il Masked Language Model e la Next Sentence Prediction. Per adattare efficacemente questo modello di base a uno scenario specifico di ranking, è necessario ricorrere a un processo di fine-tuning. Nello specifico, l'approccio descritto da Nogueira et al. (pubblicato su arXiv nel 2019) impiega un modello di tipo Point-wise abbinato a un approccio di addestramento Pair-wise. Questo significa che il modello valuta la pertinenza di documenti positivi e negativi rispetto a una singola query, calcolando il relativo Positive Score e Negative Score. Per ottimizzare questi punteggi e addestrare la rete, viene utilizzata una funzione di perdita basata sull'entropia incrociata, ovvero la Cross-entropy loss. Matematicamente, la funzione di perdita di questo modello, denominata $L_{mono}$, è definita dalla seguente equazione matematica:

$$L_{mono}=-\sum_{j\in J_{pos}}log(s_{j})-\sum_{j\in J_{neg}}log(1-s_{j})$$

[INSERIRE IMMAGINE: Diagramma del processo di fine-tuning di MonoBERT. Raffigura una Query che viene processata insieme a un Positive Document e a un Negative Document all'interno del Vanilla BERT. Vengono generati rispettivamente un Positive Score ($s^{+}$) e un Negative Score ($s^{-}$), i quali confluiscono nel calcolo finale della Cross-entropy loss]

### Le Straordinarie Performance di MonoBERT

I risultati ottenuti applicando il fine-tuning a BERT sono notevoli. È stato dimostrato che un modello linguistico di uso generale (general purpose), se adeguatamente affinato esclusivamente sul task specifico di interesse, è in grado di superare le soluzioni che in precedenza rappresentavano lo stato dell'arte (SOTA). Di fatto, le prestazioni di MonoBERT sono sbalorditive anche con una quantità di dati ridotta. Sono sufficienti appena 100.000 coppie di addestramento (training pairs), che rappresentano solamente lo 0,3% dei dati di training completi, per ottenere risultati superiori ai modelli precedenti. Questo set di addestramento ridotto è stato generato a partire da 10.000 query, per ognuna delle quali sono stati estratti 10 passaggi rilevanti utilizzando l'algoritmo classico BM25. Nonostante questa limitata base di partenza, BERT si dimostra fin da subito superiore alle soluzioni SOTA preesistenti.

[RIFERIMENTO VISIVO DEL PROFESSORE: Il grafico a linee mostra l'andamento della metrica MRR@10 in funzione del numero di coppie di addestramento. Si nota chiaramente come la curva del modello BERT Large superi la linea tratteggiata orizzontale, che rappresenta il precedente modello SOTA "IR-NET", non appena raggiunge la soglia delle 100k coppie, per poi stabilizzarsi a valori ancora più alti con 1M e 10M di coppie]

Per contestualizzare questo balzo in avanti, la tabella seguente, tratta dallo studio di Nogueira et al. del 2019, riassume le performance di vari modelli valutati sui dataset MS MARCO (metrica MRR@10) e TREC-CAR (metrica MAP):

| **Method**                         | **MS MARCO MRR@10 Dev** | **MS MARCO MRR@10 Eval** | **TREC-CAR MAP Test** |
| ---------------------------------- | ----------------------- | ------------------------ | --------------------- |
| BM25 (Lucene, no tuning)           | 16.7                    | 16.5                     | 12.3                  |
| BM25 (Anserini, tuned)             |                         | 15.3                     |                       |
| Co-PACRR* (MacAvaney et al., 2017) |                         | 14.8                     |                       |
| KNRM (Xiong et al., 2017)          | 21.8                    | 19.8                     |                       |
| Conv-KNRM (Dai et al., 2018)       | 29.0                    | 27.1                     |                       |
| IRNet                              | 28.1                    | 27.8                     |                       |
| BERT Base                          | 34.7                    |                          | 31.0                  |
| BERT Large                         | 35.8                    | 36.5                     | 33.5                  |

### Metodi Basati sulla Rappresentazione (Representation-based Methods)

Tra le metodologie neurali adottate per l'IR (studiate anche presso l'HPC Lab dell'ISTITUTO DI SCIENZA E TECNOLOGIE DELL'INFORMAZIONE "A. FAEDO" del Consiglio Nazionale delle Ricerche), spiccano i metodi basati sulla rappresentazione, noti come Representation-based methods. In questa architettura, ogni singolo documento $d$ (composto da $m$ token) e ogni query $q$ (composta da $n$ token) vengono elaborati e rappresentati in maniera del tutto separata sotto forma di vettori densi. Questo processo è reso possibile da un'architettura definita "Siamese", in cui due reti neurali identiche (Neural Network D e Neural Network Q) estraggono i vettori di feature. Un vantaggio cruciale di questa configurazione risiede nella divisione del carico computazionale: il calcolo per i documenti viene eseguito interamente offline (Offline Computation), mentre solamente l'elaborazione della query avviene in tempo reale (online). La rilevanza finale, indicata con il punteggio $S_{q,d}$, si ottiene misurando la similarità tra questi due vettori densi rappresentativi, tipicamente attraverso un semplice ma efficace prodotto scalare (dot product).

[INSERIRE IMMAGINE: Diagramma dell'Offline Computation per i Metodi Basati sulla Rappresentazione. Mostra due flussi paralleli: il Documento d processato da una Rete Neurale e la Query q processata da una Rete Neurale identica in architettura Siamese. Entrambi producono Vettori di Feature che vengono combinati tramite prodotto scalare per formare una Rappresentazione Densa e calcolare il punteggio di rilevanza finale]

A partire dal 2019, i metodi di dense retrieval hanno riscosso un enorme successo in quanto possiedono numerose proprietà desiderabili. In primo luogo, offrono una rappresentazione testuale completamente apprendibile (fully-learnable representation) e si integrano in modo molto naturale con i processi di fine-tuning. Inoltre, garantiscono un'elevata efficienza operativa, in quanto supportano tecniche avanzate di ricerca rapida, come l'Approximate Nearest Neighbor (ANN) search. Infine, e forse la cosa più importante, questi metodi superano le tradizionali limitazioni del recupero di informazioni sparso (sparse retrieval), eliminando definitivamente il problema del vocabulary mismatch, ovvero la discrepanza di vocabolario tra i termini cercati e quelli presenti nel testo.

### L'Evoluzione verso le Rappresentazioni Dense (Dense Representations)

Alla base dei metodi descritti vi sono le Rappresentazioni Dense. Queste tecniche apprendono come proiettare i vettori di feature sparsi all'interno di uno spazio continuo a bassa dimensionalità, indicato come $R^{k}$, dove $k$ è strettamente minore della dimensione del vocabolario originale ($k\ll|F|$). Le fondamenta di questo campo sono state gettate da **Word2Vec**, introdotto da Mikolov et al. nel 2013. Questo modello propone due distinti approcci per apprendere le rappresentazioni vettoriali continue delle parole. Il primo, denominato **CBOW** (Continuous Bag of Words), cerca di predire una specifica parola partendo dal suo contesto circostante. Il secondo approccio, lo **Skip-gram**, opera al contrario: tenta di predire il contesto a partire da una singola parola fornita. Dal punto di vista architetturale, entrambi gli approcci sono implementati come reti neurali lineari a due strati, in cui le parole in ingresso e in uscita (originariamente nel formato sparso one-hot) vengono codificate e successivamente decodificate in una rappresentazione densa dotata di dimensioni inferiori. Un aspetto rivoluzionario di questa tecnica è che non richiede alcun tipo di dato etichettato manualmente dall'uomo (no need for human-labeled data). Inoltre, regolando la dimensione della finestra di contesto (context window size), è possibile variare l'obiettivo dell'apprendimento: finestre più ampie permettono di catturare maggiormente la semantica, mentre finestre più brevi si focalizzano sulla sintassi del linguaggio.

A seguito di Word2Vec, sono emersi approcci via via più sofisticati. Tra questi, **FastText** estende il modello di base includendo anche gli n-grammi di una parola. Di conseguenza, l'embedding finale di un termine risulta essere la somma vettoriale del suo embedding specifico e dell'embedding di tutti i suoi n-grammi costituenti. Un'ulteriore evoluzione è rappresentata da **Doc2Vec** (Mikolov e Le, ICML 2014). Questa architettura estende Word2Vec aggiungendo nuove dimensioni di input specificatamente dedicate agli identificatori dei documenti (document IDs). In questo modo, gli ID dei documenti vengono proiettati nel medesimo spazio vettoriale delle parole, permettendo di derivare l'embedding di un intero documento a partire dai vettori delle parole che lo compongono. Tali embedding documentali si rivelano estremamente versatili e possono essere impiegati per una moltitudine di task computazionali. L'apice attuale di questa evoluzione, come descritto nello studio di Delvin et al. del 2019, è l'impiego di **BERT** per generare *contextualized embeddings*, in cui la rappresentazione vettoriale di una determinata parola non è statica, ma varia in modo dinamico a seconda dell'esatto contesto in cui si trova inserita all'interno della frase.

### Configurazione dei Modelli e Scelta tra Rappresentazione Singola e Multipla

Negli ultimi anni, la ricerca ha prodotto diversi contributi fondamentali sull'utilizzo delle rappresentazioni dense in ambito IR. Modelli di spicco includono **ColBERT** (Khattab e Zaharia, ACM SIGIR 2020), **ANCE** (Xiong et al., ICLR 2021), e la combinazione **STAR / ADORE** (Zhan et al., ACM SIGIR 2021). Una distinzione strutturale cruciale in questi sistemi è quella tra metodi a singola rappresentazione (Single-representation methods) e metodi a rappresentazione multipla (Multiple-representation methods). Nei modelli a rappresentazione singola, come ad esempio ANCE, viene generato un unico embedding vettoriale per codificare l'intero documento. Al contrario, nei metodi a rappresentazione multipla, come ColBERT, il documento viene espresso attraverso un insieme di embedding, assegnandone uno specifico per ogni singolo termine contenuto al suo interno.

### Le Funzioni di Loss e la Strategia di Selezione dei Dati Negativi

L'addestramento (learning) volto a creare queste rappresentazioni dense per l'Information Retrieval richiede campioni di dati strutturati solitamente in coppie o triple di input. Un campione tipico è formato da una query $q$, da un documento positivo pertinente $d^{+}$, e da un documento negativo $d^{-}$. Per ciascuno di questi elementi si ottiene un vettore di embedding. Successivamente, si impiega una specifica funzione metrica per misurare la similarità tra queste rappresentazioni nello spazio denso; le scelte più comuni ricadono sulla Similarità del Coseno (Cosine similarity) o sul Prodotto Scalare (Dot product). Lo scopo dell'addestramento è forzare il modello a produrre embedding geometricamente vicini per gli input tra loro simili e, specularmente, vettori distanti per gli input discordanti. Questo concetto viene formalizzato attraverso funzioni di loss che quantificano le distanze relative tra i diversi campioni analizzati.

La misurazione avviene tramite logiche di Pairwise o Triplet loss. In uno scenario ideale, la distanza vettoriale tra la query $q$ e il documento pertinente $d^{+}$ dovrebbe tendere a 0, mentre la distanza tra la query $q$ e il documento non pertinente $d^{-}$ dovrebbe essere superiore a un certo margine prefissato $m$. La formulazione matematica della loss in questo caso è:

$$L(q,d)=\begin{cases}d(q,d) & se\ d\in D^{+}\\ max(0,m-d(q,d)) & se\ d\in D^{-}\end{cases}$$

Mentre l'individuazione dei documenti positivi risulta agevole in quanto derivano direttamente dai dati di supervisione forniti al modello, la selezione dei documenti negativi rappresenta una sfida molto più complessa (How to choose them?). Nel tempo sono state sviluppate diverse strategie di campionamento, quali la selezione in-batch (all'interno dello stesso lotto di dati), la selezione cross-batch e il campionamento casuale (random sampling).

[INSERIRE IMMAGINE: Diagramma a cerchi concentrici che illustra la classificazione dei negativi nello spazio geometrico. Al centro vi è il punto rosso della query (q) e vicino ad essa il punto blu del documento positivo (d). Lo spazio circostante, delimitato dal raggio del "margin", contiene gli Hard Negatives (i negativi più difficili da distinguere perché molto vicini alla query). Esternamente vi sono gli anelli dei Semi-Hard Negatives e, ancora più lontani, gli Easy Negatives]

La classificazione di questi documenti negativi riflette la loro distanza dalla query nello spazio vettoriale: gli **Easy Negatives** sono documenti facilmente riconoscibili come errati poiché estremamente discordanti; i **Semi-Hard Negatives** presentano una moderata similarità; infine, gli **Hard Negatives** sono documenti non rilevanti ma che possiedono un'altissima somiglianza con la query, rendendoli gli esempi negativi più importanti e complessi da gestire durante il training.

---

### Glossario e Concetti Chiave

- **Cross-entropy loss e Pair-wise training**: Tecnica di apprendimento per il fine-tuning di BERT in cui la perdita viene calcolata confrontando i punteggi di documenti positivi e negativi rispetto a una singola query.

- **Architettura Siamese**: Struttura neurale alla base dei metodi orientati alla rappresentazione, che impiega due reti identiche e parallele per computare separatamente i vettori densi di documenti e query prima del loro confronto.

- **Dense Retrieval & Embeddings (Word2Vec, Doc2Vec, FastText)**: Approcci che abbandonano la rappresentazione sparsa a favore della proiezione del testo in vettori continui a bassa dimensionalità, capaci di catturare a fondo sintassi e semantica.

- **Single vs. Multiple Representation**: Distinzione architetturale fondamentale tra metodi che usano un solo vettore per sintetizzare l'intero documento (come ANCE) e metodi che assegnano un vettore per ogni singolo termine (come ColBERT).

- **Triplet loss e Selezione dei Negativi**: Metrica di distanza usata per l'addestramento, che mira a distanziare i documenti non pertinenti (classificabili in Easy, Semi-Hard e Hard Negatives) oltre un certo margine, avvicinando contemporaneamente i documenti pertinenti alla query.

---

# Architetture di Retrieval: Campionamento Dinamico e Late Interaction

**Introduzione**

Dopo aver esplorato i concetti base del dense retrieval e l'addestramento dei modelli linguistici, è fondamentale comprendere le sfide legate all'efficienza e alla scalabilità di questi sistemi. In questa sezione analizzeremo come i motori di ricerca gestiscono il calcolo delle similarità tramite algoritmi di ricerca approssimata, per poi concentrarci su due architetture avanzate che affrontano il problema dei falsi positivi e dei costi computazionali: ANCE e ColBERT.

### L'Importanza dei Negativi Difficili e il Campionamento Dinamico

Come abbiamo visto, l'addestramento di modelli di retrieval richiede l'uso di esempi negativi, ma non tutti i negativi sono uguali. I negativi difficili, o **Hard Negatives (HN)**, si rivelano essere in assoluto i più importanti per insegnare al modello a distinguere le sfumature semantiche sottili. Esistono due strategie principali per selezionarli: il **Campionamento Statico** (Static Sampling) e il **Campionamento Dinamico** (Dynamic Sampling). Nel campionamento statico, i negativi difficili vengono pre-calcolati prima dell'inizio dell'addestramento e non cambiano mai durante l'intero processo. Al contrario, il campionamento dinamico è un approccio molto più sofisticato: i negativi difficili sono selezionati dinamicamente calcolandoli a ogni singolo passo di addestramento (training step). In pratica, si scelgono i documenti irrilevanti che il modello in quello specifico istante sta erroneamente posizionando in cima alla classifica (top-ranked irrelevant documents).

Per comprendere appieno questa differenza, possiamo fare un esempio concreto. Supponiamo che l'utente cerchi "Presidente della Repubblica Italiana". Un **Random Negative** (negativo facile o casuale) potrebbe essere un documento intitolato "Come cucinare la pasta", che non ha alcuna correlazione e risulta facile da scartare. Un **Hard Negative** statico potrebbe essere "Presidente degli Stati Uniti", che ha un'alta correlazione testuale ma un intento diverso. Il vero potere del **Dynamic Hard Negative** si manifesta con documenti contenenti dati semanticamente molto rilevanti ma obsoleti o errati nel contesto specifico, come ad esempio un documento su "Giorgio Napolitano" (Presidente dal 2006 al 2015), che il modello potrebbe inizialmente confondere prima di affinarsi per favorire il risultato corretto "Sergio Mattarella" (dal 2015 in poi).

### Il Flusso dei Motori di Ricerca e il k Nearest Neighbour

Per capire come questi modelli si inseriscono in un motore di ricerca reale, dobbiamo guardare alla sua architettura di base. Il flusso di lavoro tradizionale inizia con le **Queries** e i **Texts** (documenti) che passano attraverso un **Inverted Index** (indice invertito). Questo indice alimenta una fase di **Initial Retrieval** (recupero iniziale), la quale produce un sottoinsieme di **Candidate Texts** (testi candidati). Questi candidati vengono infine passati a un **Reranker**, che ha il compito di riordinarli in modo raffinato per produrre la **Ranked List** (lista ordinata) finale che l'utente visualizzerà.

[INSERIRE IMMAGINE: Diagramma del funzionamento di un motore di ricerca, che illustra il flusso logico dai testi elaborati in un indice invertito, passando per il recupero iniziale dei testi candidati, fino ad arrivare al reranker che genera la lista ordinata finale]

Nei sistemi che adottano le **Rappresentazioni Dense** (Dense Representations), il recupero delle informazioni si traduce nel trovare i documenti i cui vettori sono geometricamente più simili a quello della query, calcolando il prodotto scalare (dot product) o la similarità del coseno (cosine similarity). Questo compito equivale a cercare i $k$ punti più vicini in uno spazio a N-dimensioni, noto come **k Nearest Neighbour (kNN) Retrieval**. Effettuare una ricerca kNN esatta (Exact kNN) richiede di confrontare tutti i documenti $d$ contro la query $q$, mantenendo in memoria una struttura dati di tipo *Heap* per conservare i $k$ elementi con la similarità più alta. Tuttavia, questo approccio ha una complessità temporale di $O(n \log k)$ e risulta troppo lento per sistemi su larga scala. Di conseguenza, si ricorre all'**Approximate Nearest Neighbour (ANN)**, che baratta una frazione di precisione per un'enorme velocità, utilizzando diverse tecniche avanzate come Alberi (Trees), Grafi (Graphs), Locality-Sensitive Hashing (LSH) o Quantizzazione.

### ANCE: Approximate Nearest-Neighbor Negative Contrastive Estimation

Una delle implementazioni più efficaci del campionamento dinamico è il modello **ANCE**. Questo sistema si basa sull'idea che il campionamento dei negativi difficili sia un elemento assolutamente cruciale. ANCE utilizza i negativi difficili dinamici sfruttando costantemente il modello appreso fino a quel momento per selezionarli. Architetturalmente, utilizza un **BERT Siamese/Dual Encoder**, una tecnica a rappresentazione singola (Single-representation technique) in cui viene generato esattamente un vettore per ogni documento e in cui i pesi della rete sono condivisi tra la query e il documento. Questo permette di effettuare il recupero esatto tramite KNN sulla base dei vettori generati.

Il processo di addestramento di ANCE è particolarmente innovativo perché separa logicamente le figure del **Trainer** e dell'**Inferencer**. Mentre il Trainer aggiorna i pesi del modello utilizzando un mix di documenti positivi e negativi, l'Inferencer lavora in parallelo (o in modo asincrono) effettuando l'inferenza per aggiornare l'indice e la ricerca ai vari checkpoint (es. $k-1$, $k$, $k+1$). Questo aggiornamento costante dell'indice garantisce che l'insieme dei negativi di ANCE (ANCE Negatives, indicati come $D_{f_{k}}^{-}$) sia sempre allineato con le debolezze attuali del modello.

[INSERIRE IMMAGINE: Schema del processo di addestramento di ANCE, con l'alternanza parallela tra il Trainer, che elabora documenti positivi e negativi, e l'Inferencer, che ad ogni checkpoint temporale aggiorna dinamicamente l'indice di ricerca dei negativi difficili]

Questa architettura complessa porta a risultati di altissimo livello. La seguente tabella illustra le performance di ANCE (sviluppato da Xiong et al., ICLR 2021) rispetto ad altre metodologie sparse, in cascata e dense sui dataset MS MARCO e TREC DL:

| **Method**                               | **MARCO Dev Passage Retrieval (MRR@10)** | **MARCO Dev Passage Retrieval (Recall@1k)** | **TREC DL Passage Rerank (NDCG@10)** | **TREC DL Passage Retrieval (NDCG@10)** | **TREC DL Document Rerank (NDCG@10)** | **TREC DL Document Retrieval (NDCG@10)** |
| ---------------------------------------- | ---------------------------------------- | ------------------------------------------- | ------------------------------------ | --------------------------------------- | ------------------------------------- | ---------------------------------------- |
| **Sparse & Cascade IR**                  |                                          |                                             |                                      |                                         |                                       |                                          |
| BM25                                     | 0.814                                    | 0.240                                       | 0.506                                | 0.519                                   |                                       |                                          |
| Best DeepCT                              | 0.243                                    | n.a.                                        | n.a.                                 | 0.554                                   |                                       |                                          |
| Best TREC Trad Retrieval + BERT Reranker | 0.240                                    | n.a.                                        | 0.554                                | 0.742                                   | 0.549                                 | 0.646                                    |
| **Dense Retrieval**                      |                                          |                                             |                                      |                                         |                                       |                                          |
| Rand Neg                                 | 0.949                                    | 0.261                                       | 0.552                                | 0.605                                   | 0.615                                 | 0.543                                    |
| NCE Neg                                  | 0.256                                    | 0.943                                       | 0.602                                | 0.539                                   | 0.618                                 | 0.542                                    |
| BM25 Neg                                 | 0.299                                    | 0.928                                       | 0.664                                | 0.591                                   | 0.626                                 | 0.529                                    |
| DPR (BM25 + Rand Neg)                    | 0.311                                    | 0.952                                       | 0.600                                | 0.653                                   | 0.629                                 | 0.557                                    |
| BM25- Rand                               | 0.280                                    | 0.948                                       | 0.576                                | 0.609                                   | 0.566                                 | 0.637                                    |
| BM25 → NCE Neg                           | 0.942                                    | 0.279                                       | 0.608                                | 0.571                                   | 0.638                                 | 0.564                                    |
| BM25 → BM25 + Rand                       | 0.939                                    | 0.306                                       | 0.648                                | 0.591                                   | 0.540                                 | 0.626                                    |
| ANCE (FirstP)                            | 0.959                                    | 0.330                                       | 0.648                                | 0.677                                   | 0.615                                 | 0.641                                    |
| ANCE (MaxP)                              |                                          |                                             | 0.628                                | 0.671                                   |                                       |                                          |

### ColBERT: Contextualized Late Interaction over BERT

Nonostante i vantaggi del Dual Encoder (veloce ma meno preciso) e del Cross-Encoder (preciso ma estremamente lento), la ricerca ha cercato un punto di incontro. La risposta è **ColBERT** (Contextualized Late Interaction over BERT), un modello differenziabile end-to-end che mischia abilmente la velocità del primo con la precisione del secondo. L'intuizione principale di ColBERT è la **Late Interaction** (interazione ritardata), ideata specificamente come metodo per combattere il pesante onere computazionale.

A differenza di ANCE, ColBERT impiega una rappresentazione basata sui termini (Term-based representation), in cui ogni singolo documento è rappresentato da un insieme di vettori, ovvero uno per ogni suo termine. Questi vettori densi dei documenti possono essere comodamente pre-calcolati offline. Poiché le query sono sconosciute a priori, le loro rappresentazioni contestualizzate vengono calcolate on-line. L'interazione "tardiva" avviene in questo modo: per ogni singolo token che compone la query, si calcola la similarità con *tutti* i token presenti nel documento, estraendo poi il valore massimo (operatore MaxSim). La similarità finale tra la Query $Q$ e il Documento $D$ è data dalla sommatoria di tutte queste similarità massime, e il recupero finale sfrutta un kNN approssimato (Approximate kNN retrieval). Matematicamente, questo concetto si esprime con la formula:

$S_{q,d} := \sum_{i\in[|E_{q}|]} max_{j\in[|E_{d}|]} E_{q_{i}} \cdot E_{d_{j}}^{T}$

[INSERIRE IMMAGINE: Architettura di ColBERT che illustra il meccanismo di Late Interaction, dove le molteplici rappresentazioni vettoriali dei termini della query interagiscono parallelamente con i termini del documento tramite l'operatore matematico MaxSim, per poi essere sommate e restituire lo score finale]

Questa architettura riduce drasticamente i FLOPs (operazioni in virgola mobile) per query rispetto a un approccio congiunto standard. Nella tabella seguente, tratta dallo studio di Khattab e Zaharia (ACM SIGIR 2020), possiamo osservare come ColBERT mantenga un MRR@10 eccezionale abbattendo drasticamente la latenza e il carico computazionale:

| **Method**                   | **MRR@10 (Dev)** | **MRR@10 (Eval)** | **Re-ranking Latency (ms)** | **FLOPs/query** |
| ---------------------------- | ---------------- | ----------------- | --------------------------- | --------------- |
| BM25 (official)              | 16.7             | 16.5              |                             |                 |
| KNRM                         | 19.8             | 19.8              | 3                           | 592M (0.085x)   |
| Duet                         | 24.3             | 24.5              | 22                          | 159B (23x)      |
| fastText+ConvKNRM            | 29.0             | 27.7              | 28                          | 78B (11x)       |
| $BERT_{base}$ [25]           | 34.7             |                   | 10,700                      | 97T (13,900x)   |
| $BERT_{base}$ (our training) | 36.0             |                   | 10,700                      | 97T (13,900x)   |
| $BERT_{large}$ [25]          | 36.5             | 35.9              | 32,900                      | 340T (48,600x)  |
| ColBERT (over $BERT_{base}$) | 34.9             | 34.9              | 61                          | 7B (1x)         |

[RIFERIMENTO VISIVO DEL PROFESSORE: Grafico a dispersione che confronta la latenza delle query in millisecondi sull'asse delle ordinate con l'accuratezza (MRR@10) sull'asse delle ascisse dei vari modelli. Il grafico evidenzia chiaramente come ColBERT, sia in modalità re-rank che full retrieval, offra un eccellente compromesso posizionandosi in basso a destra, garantendo alta precisione a una frazione del tempo richiesto da BERT-base e BERT-large]

---

### Glossario e Concetti Chiave

- **Hard Negatives e Dynamic Sampling**: Documenti semanticamente molto simili alla query ma errati. Il campionamento dinamico li estrae in tempo reale in base alle classifiche erronee del modello durante il training, fornendo gli esempi più istruttivi per l'apprendimento.

- **Approximate Nearest Neighbour (ANN)**: Tecniche algoritmiche (come LSH o alberi) utilizzate per superare il limite computazionale $O(n \log k)$ della ricerca kNN esatta, permettendo di trovare velocemente i vettori più simili in dataset di grandi dimensioni.https://meet.google.com/fvi-pvcf-ttu

- **ANCE (Approximate Nearest-neighbor Negative Contrastive Estimation)**: Un modello basato su Dual Encoder che migliora le proprie prestazioni aggiornando asincronamente (tramite un Inferencer) l'indice dei negativi difficili durante il processo di training.

- **ColBERT e Late Interaction**: Un'architettura che mantiene vettori multipli per ogni termine del documento testuale. La "Late Interaction" calcola le similarità massime tra ogni termine della query e tutti i termini del documento solo all'ultimo stadio, garantendo la precisione del Cross-Encoder con latenze drasticamente inferiori.

---

# Efficienza e Ottimizzazione nei Modelli di Neural IR

L'integrazione di reti neurali profonde nei sistemi di Information Retrieval ha portato a miglioramenti eccezionali per quanto concerne l'accuratezza semantica, ma ha sollevato un problema critico imprescindibile: l'efficienza computazionale. In questo capitolo analizzeremo le sfide architetturali poste da questi potenti strumenti e capiremo come i moderni motori di ricerca affrontino la necessità di mantenere alte prestazioni riducendo drasticamente i tempi di latenza. Nello specifico, esploreremo tecniche avanzate come il calcolo anticipato delle rappresentazioni, i protocolli di compressione dei dati e modelli interpretativi innovativi come EPIC.

### L'Importanza dell'Efficienza nei Motori di Ricerca

Negli ultimi anni, le **Deep Transformer Networks** hanno superato i precedenti standard qualitativi (diventando lo stato dell'arte, o SOTA) in una moltitudine di task legati all'elaborazione del linguaggio naturale e all'Information Retrieval. L'incredibile precisione di questi modelli comporta tuttavia un costo elevato: le loro dimensioni sono colossali, rendendone l'esecuzione dal vivo estremamente esosa. Per fornire una dimensione a questa crescita esponenziale, basti pensare che nel giro di un solo anno i modelli linguistici pre-addestrati sono passati dai circa 110 milioni di parametri del primo GPT agli oltre 8,3 miliardi di Megatron-LM, arrivando infine alla sbalorditiva cifra di 175 miliardi di parametri per GPT-3. L'applicazione di queste enormi architetture a un motore di ricerca web classico (Ad-hoc Retrieval) deve scontrarsi con vincoli di tempo estremamente severi: il sistema è obbligato a restituire all'utente i "top-k" documenti più rilevanti per una data query in un lasso temporale che solitamente non deve superare i 100 millisecondi. Date le premesse, i modelli neurali generano un impatto e un peso sostanziale sulle performance di elaborazione delle query stesse. Per aggirare questo collo di bottiglia elaborativo, la comunità scientifica ha proposto svariate metodologie, tra le quali spiccano l'interazione disaccoppiata (**De-coupled Interaction**), meccanismi di **Knowledge Distillation**, la **Quantizzazione** e procedure per uscire precocemente dai rami di calcolo (**Early exit**).

### Il Compromesso tra Latenza ed Efficacia

Il divario tecnico tra l'accuratezza predittiva dei modelli linguistici massivi e la loro reale velocità operativa è immediatamente comprensibile confrontando empiricamente i tempi di inferenza con le metriche di efficacia.

[RIFERIMENTO VISIVO DEL PROFESSORE: Il grafico a dispersione posiziona i modelli su un piano cartesiano confrontando la metrica MRR@10 sull'asse verticale con la latenza della query in millisecondi sull'asse orizzontale. È evidente come la versione "BERT (large)" domini in alto a destra, esibendo prestazioni di ranking eccellenti ma con una latenza proibitiva oltre i 3500ms. In basso a sinistra risiede l'algoritmo classico BM25, istantaneo ma poco efficace (MRR@10 di circa 0.20). Nelle aree centrali del grafico sono sparsi svariati modelli di compromesso, come doc2query, Duet (v2), DeepCT-Index, docTTTTTquery e le architetture TK a 1 o 3 livelli.]

Come è facile dedurre da questa analisi visiva, modelli statistici tradizionali quali BM25 assicurano risposte quasi istantanee, accettando di contro un'accuratezza decisamente inferiore rispetto all'avanguardia neurale. Diametralmente all'opposto, l'imponente architettura BERT "large" tocca i vertici dell'efficacia nel riordinamento dei documenti, richiedendo però finestre di calcolo inaccettabili per il rilascio in un ambiente web in tempo reale. La grande scommessa tecnologica è dunque concepire soluzioni algoritmiche ibride capaci di spingere i propri risultati verso l'angolo in alto a sinistra dello schema di dispersione: l'obiettivo è emulare, o quantomeno avvicinare, l'altissimo ranking di BERT operando a una pura frazione del suo dispendio temporale.

### Disaccoppiare l'Architettura per Precalcolare i Termini

Per studiare l'ottimizzazione del modello, è necessario fare un passo indietro all'architettura standard di elaborazione. Quando si usa BERT nella sua forma nativa, i testi della query e del documento vengono uniti fin dall'inizio e somministrati contemporaneamente in pasto alla rete: l'input inizia con il token di classificazione **[CLS]**, seguito dai token della query testuale (es. "tax" ed "evade"), prosegue con il divisore **[SEP]**, aggancia i vocaboli del documento in esame (come "world", "news", "for", "tax", "fraud", "today"), e chiude con un secondo **[SEP]**. Tutta questa sequenza attraversa integralmente e congiuntamente svariati livelli strutturali noti come **Self-attention layers**, per poi produrre solo alla fine il punteggio di rilevanza desiderato (**ranking score**) pesato mediante un modulo denominato $W_{combine}$.

[INSERIRE IMMAGINE: Diagramma raffigurante l'architettura convenzionale di BERT, illustrando la concatenazione diretta di Query e Documento (delimitati dai token speciali [CLS] e [SEP]), i cui vettori attraversano congiuntamente un blocco coeso di livelli di Self-attention fino all'estrazione dello score finale.]

Alla luce di questo processo, i ricercatori hanno avanzato un'ipotesi decisiva: le primissime fasi di auto-attenzione tra la query immessa e il testo del documento sono davvero determinanti per definire se un file è rilevante? Indagini approfondite hanno rivelato che tali passaggi embrionali non sono cruciali (spoiler: not really). Su questo assunto è stata costruita una via per il riordino efficiente dei documenti basata sul calcolo anticipato e offline delle rappresentazioni vettoriali dei termini testuali, ovvero il **Precomputing Term Representations**. Il concetto è lineare ma potentissimo: la query e il testo del documento possono essere tranquillamente elaborati in maniera separata e asincrona nei primi strati (o layer) dell'architettura Transformer, restituendo un drastico salto in avanti nella velocità di inferenza del software.

[RIFERIMENTO VISIVO DEL PROFESSORE: L'istogramma illustra la tenuta delle metriche P@20 e ERR@20 all'aumentare dell'indice del "Join Layer", ossia il livello esatto in cui query e documento convergono e iniziano a condividere la self-attention. L'immagine certifica che posticipando progressivamente questa congiunzione (dal layer 0 fino all'11) le metriche prestazionali reggono ben al di sopra del baseline stabilito da BM25, con un decadimento evidente della qualità unicamente in prossimità dell'ultimo strato disponibile.]

Applicando concretamente questo disaccoppiamento logico, si possono processare i testi e memorizzare su dischi fisici (**Storage**) le pesanti rappresentazioni vettoriali del documento estrapolate fino al livello $l$. Contestualmente, quando il sistema riceve una ricerca, la stringa testuale della query passa indipendentemente attraverso i propri livelli iniziali, entrando finalmente in contatto incrociato col documento salvato solamente partendo dallo strato successivo $l+1$, fino alla fine della rete nel livello $n$.

### Il Collo di Bottiglia dello Storage e la Compressione PreTTR

Il disaccoppiamento dell'architettura alleggerisce in modo impressionante il tempo richiesto alla CPU/GPU del server, al prezzo tuttavia di generare un gargantuesco fabbisogno di archiviazione sui dischi di memoria (il cosiddetto storage burden). Risolvere questo dilemma ingegneristico ha richiesto l'incorporazione nel modello di due moduli intermedi: uno per comprimere i dati del documento (**comp.**) prima che siano riversati all'interno dello Storage, e l'altro speculare preposto alla decompressione rapida (**decomp.**) che ricomponga i vettori non appena devono essere iniettati nel livello condiviso $l+1$. I test eseguiti sul dataset WebTrack (su metrica P@20) hanno certificato il successo di tale integrazione: è concesso applicare tassi di compressione fino all'83% senza registrare alcuna decurtazione netta dell'accuratezza predittiva. I numeri lo dimostrano: confrontando il modello originale BERT base, dotato di un parametro P@20 di 0.3460, con una variante ottimizzata PreTTR avente un **Join Layer** ritardato fino a livello 11 ($l=11$) e vettori deflazionati dell'83% ($e=128$), quest'ultima riesce a preservare un solidissimo parametro di 0.3370.

[INSERIRE IMMAGINE: Riproduzione visiva del modello "Precomputing Term Representations" in cui i flussi paralleli e indipendenti attraversano i layer basali, affluiscono in nodi di compressione (comp.) per essere immagazzinati in uno Storage, dai quali fuoriescono poi attraverso i nodi di decompressione (decomp.) prima di riversarsi fusi nel layer l+1 superiore.]

Sotto l'aspetto cronometrico, valutando il tempo speso in secondi per vagliare lotti di 100 documenti testuali, il guadagno temporale cresce in maniera direttamente proporzionale al posizionamento del **Join Layer** nella pila neurale. Se la congiunzione tra query e documento si innesca al livello 8, il sistema elargisce uno **speedup di 3.5x**; se tuttavia il punto di unione (joint layer) viene esasperato fino allo strato numero 11, il motore registra una velocizzazione estrema, pari a un vertiginoso **42x speedup** complessivo.

### Efficienza e Interpretabilità: Il Modello EPIC

La ricerca sull'ottimizzazione del neural IR ha generato non solo evoluzioni algoritmiche, ma modelli originali dedicati come **EPIC**, acronimo di Expansion via Prediction of Importance with Contextualization. Questa architettura sorge con un duplice mandato: fendere il costo inferenziale pur garantendo un grado eccellente di trasparenza intellegibile da parte umana (interpretabilità) sulle logiche decisionali. Dal punto di vista strutturale, l'accelerazione matematica di EPIC si poggia su un duplice principio. Prima di tutto, la rete attribuisce lo score documentale impiegando puramente il dot product (prodotto scalare), beneficiando a pieno regime delle librerie computazionali ottimizzate per moltiplicazioni rapide fra matrici. In seconda battuta, EPIC argina lo spreco di risorse invocando algoritmi di natura prettamente neurale unicamente in veste di **re-ranking**, applicandoli cioè come rifinitura per scremare un limitato insieme di documenti precedentemente rintracciato e contrassegnato come "potenzialmente rilevante" in una passata pre-computazione classica (come l'estrazione primigenia via BM25). In questo modello trasparente, i vettori generati non sono mere stringhe numeriche, ma si associano a degli appositi punteggi di peso appresi per ogni vocabolo (**Importance scores**); pesi logici che la macchina sa mappare direttamente al posizionamento fisico interno alla stringa di input.

[RIFERIMENTO VISIVO DEL PROFESSORE: L'immagine antropomorfizza la testa del motore BERT mentre incamera i singoli blocchi sillabici della domanda "How far does AAA tow in California". Sulla destra è ritratta la matrice degli output con bande grigie variamente scurite: questi rappresentano visivamente gli "Importance scores" a intensità variabile estratti indipendentemente, vocabolo per vocabolo, provando la natura interpretabile del modulo.]

---

### Glossario e Concetti Chiave

- **De-coupled Interaction**: Filosofia ingegneristica atta ad arginare la dispendiosa latenza processando preventivamente ma asincronamente i vettori testuali del documento e quelli della query, ritardandone volontariamente il fatidico punto di scambio per abbassare il monte ore di calcolo in tempo reale.

- **Join Layer**: Punto fisico all'interno dei multistrati dell'infrastruttura di una rete neurale in cui l'analisi indipendente dei due flussi testuali viene interrotta, obbligando la query testuale ad avviare la correlazione semantica (self-attention) con il bagaglio del documento per estrarne il rank.

- **Precomputing Term Representations e Compressione**: Metodologia incentrata sull'escogitare un salvataggio persistente dei tensori semantici del documento, mitigando il gigantesco handicap di Storage associato avvalendosi di robusti scaglioni compressivi (capaci di sfiorare tassi deflattivi dell'83%).

- **EPIC (Expansion via Prediction of Importance with Contextualization)**: Raffinato applicativo neurale architettato per la riduzione dei costi operativi. Fonde la parsimonia derivante dall'estrazione del ranking con vettori scalari (dot product) alle iniezione trasparenti di "Importance Scores", ponderazioni di peso ricollegabili individualmente a ciascun tassello testuale letto, promuovendo così l'interpretabilità.

---

# Espansione e Interpretabilità nel Neural IR: Il Modello EPIC

**Introduzione**

Nelle architetture di Information Retrieval neurale, la necessità di bilanciare un'elevata precisione con tempi di risposta rapidi ha portato allo sviluppo di approcci ibridi e innovativi. In questa sezione, approfondiremo il funzionamento del modello EPIC (Expansion via Prediction of Importance with Contextualization), esplorando come questo sistema riesca a codificare i documenti combinando punteggi di importanza ed espansione. Analizzeremo inoltre le tecniche di potatura (pruning) per ottimizzare le prestazioni, i risultati sperimentali che ne certificano l'efficienza rispetto ai modelli tradizionali e, infine, la sua capacità unica di mantenere l'interpretabilità dei risultati.

### Vettori dei Documenti e Punteggi di Espansione

Il cuore dell'architettura **EPIC** risiede nella sua peculiare modalità di rappresentazione testuale. In questo modello, i vettori dei documenti non sono semplici stringhe numeriche opache, ma sono strutturati per riflettere due componenti fondamentali: l'**Importance score** (il punteggio di importanza di ogni singolo termine, calcolato in base al contesto) e l'**Expansion score** (il punteggio di espansione).

[INSERIRE IMMAGINE: Diagramma del modello BERT (illustrazione basata sulle grafiche di Jay Alammar, [The Illustrated BERT, ELMo, and co. (How NLP Cracked Transfer Learning) – Jay Alammar – Visualizing machine learning one concept at a time.](http://jalammar.github.io/illustrated-bert/)) che elabora la frase "The cost of endless pools...". Dalla rete fuoriescono frecce che moltiplicano gli 'Importance scores' per gli 'Expansion scores', combinandoli in un unico vettore finale del documento (Document vector)]

Quando una frase come "The cost of endless pools" viene elaborata, la rete neurale assegna dinamicamente un peso a ciascuna parola. Questi punteggi vengono poi combinati matematicamente (attraverso una moltiplicazione) per formare il vettore definitivo del documento. Questo approccio permette al sistema non solo di capire quali termini sono centrali, ma anche di espandere il significato includendo concetti semanticamente correlati, arricchendo così la rappresentazione vettoriale prima ancora che avvenga la ricerca vera e propria.

### Il Document Quality Score e la Tecnica di Pruning

Per garantire che il sistema rimanga efficiente in fase di esecuzione, EPIC implementa due meccanismi di ottimizzazione cruciali. Il primo è il **Document Quality Score**, un punteggio qualitativo globale assegnato all'intero documento. Questo valore non viene calcolato a caso, ma è generato da un livello di rete neurale di tipo feed-forward posizionato direttamente sopra il token di classificazione **[CLS]**.

[INSERIRE IMMAGINE: Schema dell'architettura di EPIC che mostra il token speciale [CLS] passare attraverso un 'feed forward layer' per generare il 'Document Quality Score'. Parallelamente, viene illustrato il processo di 'Top k pruning' applicato alle rappresentazioni dei termini]

Il secondo meccanismo è la potatura, nota tecnicamente come **Top k pruning**. Poiché memorizzare vettori densi per ogni singola parola di un documento richiederebbe uno spazio di archiviazione (storage) insostenibile, il sistema seleziona e conserva esclusivamente le rappresentazioni dei *k* termini considerati più importanti, scartando le informazioni ridondanti o di scarso valore semantico. In questo modo, l'indice di ricerca si mantiene compatto, permettendo al contempo un recupero delle informazioni estremamente rapido.

### Analisi Sperimentale: Il Compromesso tra Efficienza ed Efficacia

Le prestazioni del modello EPIC sono state rigorosamente misurate e confrontate con altri sistemi allo stato dell'arte sul dataset di validazione MS-MARCO (Dev). L'obiettivo degli esperimenti era dimostrare come fosse possibile abbattere la latenza delle query mantenendo al contempo un'alta efficacia nel ranking, misurata attraverso la metrica MRR@10.

[RIFERIMENTO VISIVO DEL PROFESSORE: Una serie di grafici a dispersione (scatter plot) che mostrano sull'asse delle ordinate l'efficacia (MRR@10) e sull'asse delle ascisse la latenza della query in millisecondi. Nei grafici viene evidenziato progressivamente il posizionamento di EPIC rispetto a docTTTTTquery, al modello TK a un layer e infine al massiccio BERT large, collocato in alto all'estrema destra a causa della sua altissima latenza.]

I risultati empirici evidenziano i notevoli vantaggi computazionali di questo approccio. La seguente tabella sintetizza le comparazioni principali effettuate durante gli esperimenti:

| **Confronto Sperimentale**       | **Accelerazione (Speedup)** | **Variazione della Latenza** | **Efficacia del Ranking Mantenuta** |
| -------------------------------- | --------------------------- | ---------------------------- | ----------------------------------- |
| **EPIC + BM25** vs docTTTTTquery | 1.3x speedup                | Da 63ms a 48ms               | 98% dell'efficacia originale        |
| **EPIC** vs TK (1 layer)         | 6.5x speedup                | Da 445ms a 68ms              | Stessa efficacia del ranking (100%) |
| **EPIC** vs BERT (large)         | 51.5x speedup               | Da 3.5 secondi a 68ms        | 83% dell'efficacia originale        |

Come si evince dai dati, l'abbinamento di EPIC con metodi di espansione o di recupero iniziale (come BM25 o docTTTTTquery) permette di superare algoritmi più complessi come TK (1 layer) ottenendo un'accelerazione di 6.5 volte a parità di risultati qualitativi. Se paragonato al mastodontico BERT (large), EPIC sacrifica solo un 17% di efficacia per restituire un tempo di risposta ben 51,5 volte più rapido, rendendo il modello utilizzabile in un contesto di produzione reale.

### L'Interpretabilità Trasparente del Modello EPIC

Oltre all'efficienza computazionale, uno dei meriti più rilevanti di EPIC è la sua **Interpretabilità**. A differenza dei modelli neurali tradizionali, che agiscono spesso come "scatole nere" inaccessibili, EPIC permette all'operatore umano di comprendere visivamente ed esplicitamente il motivo per cui un documento è stato ritenuto rilevante o espanso.

Prendendo ad esempio la query "cost of endless pools swim spa", il sistema evidenzia i termini del documento sorgente con intensità differenti a seconda della loro importanza. In una frase esplicativa che cita "endless pools and swim spa ##s are available in a number of different price brackets...", termini come "endless", "pools", "swim", "spa" e "prices" risultano chiaramente identificati come portanti. Di conseguenza, il modello genera dinamicamente dei **Top expansion terms** logicamente deducibili: per concetti legati al costo, le parole di espansione selezionate includono "pay", "paid", "cost", "paying", "much", "what", "fee", "costs", "thing" e "spending". In contrapposizione, un modello come docTTTTTquery produrrebbe un'espansione diversa, focalizzandosi su "pool", "endless", "how", "cost", "much", "price", "doe", "swim", "build", "spa". Questa trasparenza è fondamentale per diagnosticare e raffinare il comportamento del motore di ricerca.

[INSERIRE IMMAGINE: Schermata che mostra visivamente l'interpretabilità di EPIC per la query "cost of endless pools swim spa". Il testo del documento è scomposto in singole etichette colorate (box) con diverse sfumature di colore per indicare il peso dei termini. Sotto, sono elencati esplicitamente i "Top expansion terms" e i termini espansi dal modello concorrente "docTTTTTquery"]

---

### Glossario dei Concetti Chiave

- **EPIC (Expansion via Prediction of Importance with Contextualization)**: Modello neurale ottimizzato per l'efficienza e la trasparenza, che calcola vettori basati sull'importanza dei termini nel loro contesto integrandoli con capacità di espansione semantica.

- **Expansion score**: Valore numerico assegnato all'interno del vettore del documento che codifica l'arricchimento semantico, permettendo al sistema di recuperare documenti usando termini correlati non esplicitamente presenti nella query originale.

- **Document Quality Score**: Punteggio globale assegnato all'intero testo analizzato, calcolato processando il token di base [CLS] mediante uno strato dedicato della rete neurale (feed-forward layer).

- **Top k pruning**: Tecnica di alleggerimento computazionale che scarta i vettori dei termini meno rilevanti di un documento, conservando solo i primi *k* valori per ottimizzare lo spazio di memoria richiesto.

- **Interpretabilità**: La proprietà di un sistema di Information Retrieval, accentuata in EPIC, che consente agli utenti e agli sviluppatori di capire il ragionamento interno della macchina (ad esempio visionando quali parole hanno ricevuto il peso maggiore per attivare la classificazione).

---

