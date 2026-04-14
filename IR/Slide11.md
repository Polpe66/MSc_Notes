# Slide 11: L'Utilizzo di BERT nell'Information Retrieval

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

## Metodologie Avanzate di Fine-Tuning e Rappresentazioni Dense nell'Information Retrieval

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

## Architetture di Retrieval: Campionamento Dinamico e Late Interaction

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

## Efficienza e Ottimizzazione nei Modelli di Neural IR

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

## Espansione e Interpretabilità nel Neural IR: Il Modello EPIC

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
