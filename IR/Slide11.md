# Efficient k-Approximated Nearest Neighbors Retrieval (kANN)


### Similarity Search ed Embeddings

Il processo di ricerca per similarità inizia con la trasformazione di dati di natura eterogenea in un formato computazionalmente gestibile. File testuali, tracce audio, immagini e file video vengono processati in ingresso attraverso i vari strati di una rete neurale. Questa architettura estrae le caratteristiche fondamentali dei dati e le mappa all'interno di uno spazio vettoriale, dove ogni elemento multimediale viene infine rappresentato visivamente come uno specifico punto azzurro su un piano cartesiano bidimensionale.

![[Pasted image 20260430104938.png]]

### k-Nearest Neighbour Search

Nel momento in cui il sistema riceve una **Query**, indicata negli schemi con un punto interrogativo, questa subisce il medesimo processo di trasformazione. La query viene fatta passare attraverso la rete neurale e proiettata nello spazio vettoriale preesistente, assumendo la forma di un punto rosso. A questo punto, il sistema esegue una ricerca dei vicini più prossimi (k-Nearest Neighbour), individuando un sottoinsieme di punti azzurri racchiusi all'interno di un'area di prossimità delimitata da un cerchio tratteggiato verde attorno alla query. 
![[Pasted image 20260430105140.png]]

### Approximated KNN e Forward Index

Per gestire la ricerca vettoriale a livello strutturale, il sistema si appoggia a un **Forward Index**. Questo indice archivia le coordinate esatte di ogni documento elaborato, permettendo la successiva fase di interrogazione approssimata ("Approximated KNN") per garantire una maggiore efficienza computazionale. Di seguito è riportato il contenuto del Forward Index estratto per i documenti $d_{1}$, $d_{2}$ e $d_{n}$:

|**Documento**|**Coordinate Vettoriali**|
|---|---|
|$d_{1}$|1.23 3.12 2.21 9.10 2.22 0.65 3.22|
|$d_{2}$|2.22 2.44 5.21 8.22 6.66 7.21 4.26|
|$d_{n}$|4.22 2.55 8.35 6.28 2.99 1.00 6.43|

Quando viene sottomessa una nuova query caratterizzata dallo specifico vettore 7.99 2.65 8.23 9.23 9.23 3.11 3.23, il sistema deve confrontarla con le righe del Forward Index. L'obiettivo matematico della ricerca, supponendo di voler cercare i primi 10 risultati (come indicato dall'esempio $k=10$), è trovare l'argomento minimo rispetto all'insieme dei documenti $D$, espresso dalla formula $argmin_{v\in D} s(q,v)$.

La funzione di similarità $s(q,v)$ può basarsi su metriche differenti. 
Il primo approccio calcola la **Euclidean Distance** (Distanza Euclidea), definita dall'equazione $s(q,v)=\sqrt{\sum_{i=1}^{d}(q_{i}-v_{i})^{2}}$. In alternativa, è possibile utilizzare il **Dot Product** (Prodotto Scalare), la cui formula è $s(q,v)=\sum_{i=1}^{d}(q_{i}\cdot v_{i})$. Di conseguenza, se si utilizza il prodotto scalare per determinare la similarità, la regola decisionale cambia: non si cerca più di minimizzare il risultato, bensì di massimizzarlo, passando quindi all'operazione di argmax.

---

### Glossario / Concetti Chiave

- **Similarity Search**: Processo che trasforma dati multimediali in vettori tramite reti neurali per valutarne la somiglianza geometrica all'interno di uno spazio vettoriale.
    
- **Forward Index**: Struttura dati fondamentale che memorizza i documenti ($d_{1} \dots d_{n}$) e le rispettive sequenze di coordinate numeriche per permetterne la successiva estrazione.
    
- **Euclidean Distance vs Dot Product**: Metriche per quantificare la similarità tra il vettore della query $q$ e i vettori dei documenti $v$; la prima richiede di trovare il valore minimo (argmin), la seconda il valore massimo (argmax).
---
### Il Caso Monodimensionale (1D)

Consideriamo il caso base in cui abbiamo $n$ punti disposti in uno spazio monodimensionale, ovvero dove la dimensionalità è $d=1$. Immaginiamo di avere un insieme specifico di punti, corrispondenti ai valori -5.4, -2.1, 0.5, 1.2, 2.2, 3.6, 4.5, 5.1 e 7.1. Il nostro obiettivo algoritmico in questo scenario è trovare il risultato **Top-1**, ovvero l'elemento più pertinente rispetto a una determinata ricerca.

Se decidiamo di valutare la vicinanza calcolando il Top-1 con la **Distanza Euclidea**, la funzione di similarità tra una query $q$ e un punto $v$ dello spazio è definita rigorosamente dalla formula $s(q,v)=\sqrt{(q-v)^{2}}=|q-v|$. Ipotizzando che la nostra query sia il valore 2.4, il processo per individuare il punto più vicino risulta essere estremamente efficiente. Essendo i punti disposti su una singola linea, è sufficiente eseguire una ricerca binaria (binary search) per trovare il predecessore della query. Questa specifica operazione richiede un tempo logaritmico pari a $\Theta(\log n)$ e occupa uno spazio lineare in memoria. Di conseguenza, il risultato Top-1 esatto sarà sempre, ed esclusivamente, o il predecessore o il successore della query stessa all'interno della sequenza ordinata.

In alternativa, possiamo definire la ricerca del Top-1 utilizzando il **prodotto scalare** (dot product), la cui funzione si esprime come $s(q,v)=q\cdot v$. In questo caso, determinare il risultato corretto diventa un'operazione banale (super easy): la soluzione corrisponde al valore massimo (Max) presente nell'insieme se la query ha segno positivo, oppure al valore minimo (Min) se la query ha segno negativo.

### L'Estensione al Caso Bidimensionale (2D)

Aumentando il livello di complessità, analizziamo la situazione in cui gestiamo $n$ punti all'interno di uno spazio bidimensionale, caratterizzato quindi da $d=2$. In questo contesto geometrico, la tecnica standard prevede il partizionamento dello spazio attraverso l'utilizzo del **Diagramma di Voronoi**.

![[Pasted image 20260430150750.png]]

In un Diagramma di Voronoi, il numero totale dei vertici e dei bordi (edges) si mantiene proporzionale al numero di punti, possedendo una complessità asintotica pari a $\Theta(n)$.

![[Pasted image 20260430150821.png]]

Per individuare il Top-1 in questo spazio, il sistema deve localizzare fisicamente la cella di Voronoi a cui appartiene la query in questione. Questo processo è un classico problema di **2D point location** (localizzazione di un punto in 2D), il quale può essere risolto mantenendo le stesse complessità asintotiche del caso monodimensionale: richiede infatti un tempo di $\Theta(\log n)$ e uno spazio lineare. Tuttavia, è fondamentale notare che, sebbene le complessità di tempo e spazio rimangano identiche a quelle del caso 1D, l'implementazione pratica non è più semplice e presenta sfide computazionali decisamente maggiori (more challenge).

### La Maledizione della Dimensionalità

I metodi esatti basati sul partizionamento spaziale incontrano un limite teorico critico non appena si tenta di applicarli a modelli con molte dimensioni. Questa problematica è universalmente nota come **La Maledizione della Dimensionalità** (The Curse of Dimensionality).


Il partizionamento tramite Diagrammi di Voronoi non scala bene all'aumentare della dimensionalità dello spazio. Matematicamente, la dimensione strutturale di un diagramma di Voronoi cresce in modo esplosivo fino a raggiungere una grandezza di $n^{\frac{d}{2}}$. Di conseguenza, l'operazione esatta di _point location_ in spazi multi-dimensionali finirebbe per richiedere un tempo di calcolo impraticabile pari a $\Theta((d+\log n)^{c})$, consumando al contempo uno spazio di memoria immenso pari a $\Theta(n^{d})$.

Un'alternativa per evitare la costruzione dell'indice spaziale consiste nell'effettuare una **scansione lineare** (linear scan) di tutto il dataset per ogni query. Questo approccio manterrebbe uno spazio di memoria lineare, ma richiederebbe un tempo di elaborazione pari a $\Theta(nd)$.

Entrambe queste soluzioni (il partizionamento esatto e la scansione lineare) risultano del tutto insoddisfacenti per i sistemi reali. Nonostante ciò, queste metodologie rappresentano il meglio che possiamo ottenere se pretendiamo di elaborare soluzioni matematicamente esatte. Per superare questo stallo prestazionale, è inevitabile scendere a compromessi e ammettere un certo grado di approssimazione sui risultati restituiti dal sistema. Esistono diverse misure per valutare la qualità di questa approssimazione; in questo specifico contesto, l'approssimazione viene quantificata misurando il **recall rispetto al groundtruth** (ovvero la frazione di veri risultati rilevanti recuperati rispetto alla verità di base).

---

### Glossario e Concetti Chiave

- **Distanza Euclidea e Prodotto Scalare:** Due metriche fondamentali usate nel caso monodimensionale per determinare la similarità tra una query e un punto; la prima valuta la vicinanza geometrica, la seconda proietta i vettori massimizzando o minimizzando i valori in base al segno.
    
- **Diagramma di Voronoi:** Struttura geometrica utilizzata nel caso 2D per il partizionamento spaziale, che divide il piano in celle poligonali in cui ogni punto al loro interno è più vicino al generatore della cella rispetto a qualsiasi altro punto.
    
- **Point Location:** Il problema algoritmico che consiste nel determinare in quale regione (es. cella di Voronoi) cade un punto di query interrogato.
    
- **Maledizione della Dimensionalità:** Fenomeno per cui le strutture di indicizzazione esatte perdono drasticamente efficienza temporale e spaziale all'aumentare del numero di dimensioni $d$, rendendo impraticabile la ricerca algoritmica senza approssimazioni.
- --
### Strategie per l'Approximate k-NN Retrieval

Il problema del recupero approssimato dei k vicini più prossimi, noto come **Approximate k-NN Retrieval**, può essere affrontato attraverso diverse strategie fondamentali. La prima categoria comprende le **soluzioni basate su alberi** (Tree-based solutions), di cui un esempio di spicco è l'algoritmo ANNOY sviluppato da Spotify. Questo approccio si basa sul partizionamento iterativo dello spazio vettoriale: inizialmente si divide l'insieme dei dati in due metà (Split it in two halves), successivamente si divide nuovamente (Split again), e si procede in questo modo per diverse iterazioni successive (...more iterations later) al fine di creare una struttura ad albero navigabile. 
![[Pasted image 20260430151104.png]]

Una seconda strategia si avvale delle **soluzioni basate sul clustering** (Clustering-based solutions), esemplificate dall'indice IVF presente nella libreria FAISS di Facebook. In questo metodo, lo spazio viene diviso in regioni di esplorazione, all'interno delle quali viene localizzato un vettore di query $x_q$. L'area di indagine viene controllata definendo il raggio di ricerca (search scope) tramite parametri specifici, come ad esempio il parametro _nprobe_, che nell'architettura mostrata è impostato a $8$. 
![[Pasted image 20260430151135.png]]


Il terzo approccio riguarda le **soluzioni basate sull'hashing** (Hashing-based solutions), come ad esempio la tecnica LSH derivata da pubblicazioni teoriche. Questo metodo utilizza una funzione di hashing (hashing function) per mappare un insieme di chiavi (keys) all'interno di specifici contenitori, chiamati hash buckets, associandole infine ai rispettivi valori (values) in modo da raggruppare efficientemente gli elementi simili. 
![[Pasted image 20260430151208.png]]

Infine, la strategia attualmente adottata in modo quasi universale fa uso dei **Grafi di Prossimità** (Proximity Graphs), con l'algoritmo HNSW come rappresentante assoluto. Questo approccio sfrutta una struttura a grafo multilivello per navigare lo spazio vettoriale in modo estremamente rapido: la ricerca inizia da un punto di ingresso (entry point) nel livello più alto e meno denso, per poi scendere ai livelli inferiori guidati dal vettore di query (query vector), fino a raggiungere il vicino più prossimo (nearest neighbor) nel livello base dell'infrastruttura. ![[Pasted image 20260430151309.png]]

### Simulazione In Vitro dell'Algoritmo HNSW

Per comprendere le meccaniche dei grafi di prossimità, viene proposta una "Simulazione in Vitro dell'HNSW". Questo esperimento didattico, che per sua stessa natura porta con sé il rischio intrinseco di rivelarsi un "Epic Fail", si basa su ingredienti molto semplici: gli studenti stessi, che fungono da vettori in uno spazio bidimensionale (ovvero i nodi della rete), e una specifica query. L'obiettivo finale di questa simulazione è identificare il punto _top-1_ calcolando la **distanza Euclidea** (Eucleadian distance).

La simulazione si sviluppa rigorosamente in tre fasi. Il **Step 1** consiste nella costruzione del **Grafo KNN** (o grafo di prossimità), stabilendo che per ogni nodo vi sia un numero di vicini pari a $p = 3$. Una volta stabilita la topologia iniziale, si passa al **Step 2**, in cui viene eseguito un algoritmo di ricerca di tipo _greedy_, partendo da un nodo scelto in modo del tutto casuale (random node). Durante l'esecuzione, il sistema si pone l'interrogativo fondamentale se la query sia stata trovata o meno. Nel caso di esito negativo ("No? Argh!"), significa che la ricerca è incappata in un **minimo locale** (Local Minimum). Di conseguenza, è necessario riprovare selezionando un altro punto di partenza, oppure eseguire un _backtrack_ avvalendosi di un _heap_ che memorizza i vicini. Se invece l'esito è positivo ("Yes? Urrah!"), si procede semplicemente a contare quanti passi (steps) sono stati necessari per raggiungere il traguardo.

Per ottimizzare ulteriormente questo processo e muoversi più velocemente nella "regione" in cui risiede la query, viene introdotto il **Step 3**, che prevede la creazione di una **gerarchia**. In questa fase, a ogni studente viene richiesto di pensare a un numero casuale compreso nell'intervallo $[1, 6]$ (RANDOM!). Questa meccanica stocastica serve a determinare i livelli: ogni studente che ha pensato esattamente al numero $5$ viene "promosso" al livello superiore, identificato come livello 1. A questo punto, si ripete l'operazione di costruzione del grafo descritta nel Step 1, applicandola però unicamente ai nodi isolati che compongono il neo-formato livello 1.

### NSW (Navigable Small World) e Ricerca Greedy

La struttura planare di base prima dell'introduzione dei livelli gerarchici è definita come **NSW** (Navigable Small World). Questa architettura si presenta visivamente come un grafo costituito da nodi interconnessi tra loro tramite una fitta rete di archi, all'interno della quale viene introdotto un nodo target da ricercare. ![[Pasted image 20260430151647.png]]
![[Pasted image 20260430151654.png]]
![[Pasted image 20260430151700.png]]
![[Pasted image 20260430151706.png]]

La navigazione vera e propria all'interno della rete NSW avviene tramite l'algoritmo di **Greedy Search**. La ricerca ha inizio partendo da un nodo di partenza posizionato nella struttura. L'algoritmo procede valutando i nodi adiacenti, spostandosi iterativamente verso il vicino che minimizza la distanza rispetto al nodo target rosso. Durante questo percorso esplorativo, i nodi periferici valutati vengono evidenziati dal sistema, permettendo di tracciare un percorso diretto e continuo fino ad approdare al nodo che rappresenta la migliore approssimazione spaziale della query. ![[Pasted image 20260430151731.png]]
![[Pasted image 20260430151738.png]]

![[Pasted image 20260430151750.png]]
![[Pasted image 20260430151758.png]]
![[Pasted image 20260430151805.png]]
![[Pasted image 20260430151817.png]]
![[Pasted image 20260430151828.png]]
### Concetti Chiave

- **Approximate k-NN Retrieval**: L'insieme di tecniche (basate su alberi, clustering, hashing o grafi) per identificare i k-vicini più prossimi in spazi vettoriali con alta efficienza accettando un margine di approssimazione.
    
- **HNSW (Hierarchical Navigable Small World)**: Evoluzione dei grafi di prossimità che sfrutta una gerarchia di livelli, dove i livelli superiori consentono movimenti ampi e quelli inferiori definiscono la precisione locale della ricerca.
    
- **Greedy Search**: L'algoritmo di base impiegato nei grafi NSW che, partendo da un nodo casuale, si sposta inesorabilmente verso il vicino più prossimo alla query, rischiando tuttavia di bloccarsi in minimi locali.
---

### Le Skip List di Pugh come Fondamento Teorico

L'introduzione di una gerarchia nell'algoritmo **HNSW** trae la sua ispirazione diretta dalle **Skip List di Pugh**. Questa struttura dati si propone come un'alternativa probabilistica ai classici alberi di ricerca binaria (**BST**) al fine di rispondere in modo estremamente efficiente alle interrogazioni per trovare il predecessore di un valore (predecessor queries).

![[Pasted image 20260430151901.png]]

Il fondamento di questa architettura risiede in quello che definiamo **Layer 0**. Questo strato di base non è altro che una lista collegata standard, dove tutti gli elementi del dataset (ad esempio i nodi 3, 5, 7, 11, 14, 21 e 29) sono disposti sequenzialmente e collegati dal nodo di _Start_ fino al nodo di _End_.

### Costruzione dei Livelli Gerarchici (Layers)

Se dovessimo cercare un valore nel solo Layer 0, saremmo costretti a scorrere la lista linearmente. Per ovviare a questo problema, la logica delle Skip List prevede l'aggiunta di livelli gerarchici superiori, i quali contengono versioni sempre più "sparse" della lista originale.

![[Pasted image 20260430151936.png]]

Viene quindi generato un **Layer 1**, collocato al di sopra del livello base, che ospita solamente un sottoinsieme dei nodi, come ad esempio il 5, l'11 e il 21. Di conseguenza, questi nodi agiscono come "stazioni intermedie" che permettono di saltare grandi porzioni della lista sottostante.

![[Pasted image 20260430151951.png]]

Il processo di astrazione continua costruendo strati sempre più leggeri. Viene aggiunto un **Layer 2** che contiene solamente i nodi 5 e 21, e infine un **Layer 3** che, in questo scenario, ospita esclusivamente il nodo 5.

### Esecuzione di una Query nella Struttura a Strati

Per comprendere a fondo il vantaggio di questa architettura, possiamo simulare una **query** in cui il sistema è incaricato di cercare il valore **12**.

![[Pasted image 20260430152020.png]]


La ricerca non parte dal livello base, bensì dall'entry point posizionato nel livello più alto e sparso, ovvero il **Layer 3**. Trovandosi sul nodo 5, il sistema valuta il livello corrente ma, essendo l'unico nodo disponibile, scende direttamente al **Layer 2** (sempre sul nodo 5). A questo punto, il sistema analizza il nodo successivo in orizzontale, ovvero il 21. Poiché 21 è strettamente maggiore della nostra query (12), l'algoritmo capisce di essersi spinto troppo oltre; pertanto, scende al **Layer 1** rimanendo sul nodo 5.

Nel Layer 1, il nodo successivo è l'11. Poiché 11 è minore di 12, la ricerca avanza orizzontalmente fino al nodo 11. Controllando il passo successivo da questa nuova posizione, si incontra di nuovo il 21. Essendo ancora una volta maggiore del valore cercato, la ricerca scende al livello base, il **Layer 0**, fermandosi sul nodo 11. Valutando il successivo nodo nel Layer 0 (il 14), che è maggiore di 12, l'algoritmo conclude la sua esecuzione identificando l'11 come l'esatto predecessore.

### Estensione ai Grafi di Prossimità

Sebbene l'esempio delle Skip List chiarisca il concetto su dati monodimensionali, l'algoritmo **HNSW** applica questa stessa identica logica a strutture dati complesse come i grafi geometrici o vettoriali.

![[Pasted image 20260430152131.png]]

Nei moderni sistemi di recupero delle informazioni, i dati non formano linee rette, ma reti. Si inizia con grafi composti da pochissime connessioni per arrivare, man mano, a grafi altamente densi.

![[Pasted image 20260430152146.png]]


Come illustrato per le liste, **HNSW introduce una gerarchia** sovrapponendo questi grafi. I grafi più sparsi fungono da strati superiori (Layer alti), fornendo collegamenti a lunghissimo raggio che attraversano vasti spazi vettoriali. I nodi, pur esistendo in più grafi, mantengono delle mappature esatte tra un livello e l'altro.


### Navigazione e il framework kANNolo

L'effettiva esecuzione di una ricerca all'interno di questa struttura gerarchica a grafo simula perfettamente la logica della Skip List.

![[Pasted image 20260430152220.png]]

Il punto di partenza si trova nel grafo più sparso. La ricerca (indicata visivamente da frecce verdi di instradamento) viaggia rapidamente verso l'area generale del punto bersaglio compiendo grossi balzi (long-range links). Quando non è più possibile avvicinarsi ulteriormente in quel livello, la ricerca si sposta al livello inferiore. Qui, grazie a una maggiore densità di collegamenti a medio raggio, il percorso si affina. Il ciclo si ripete fino a planare nel grafo più denso in assoluto (livello base), dove la ricerca esegue i micro-passi finali per raggiungere il nodo più vicino in assoluto (target rosso).


A supporto di queste infrastrutture gerarchiche, si posiziona il framework **kANNolo**, rappresentato iconograficamente per combinare l'idea di stratificazione dei dati (tipica del roll) con le reti a grafo sovrapposte necessarie per il calcolo approssimato dei vicini.

---

### Glossario / Concetti Chiave

- **Skip List di Pugh:** Una struttura dati probabilistica basata su liste sovrapposte e a densità decrescente, utilizzata come base logica per scalare le predecessor queries rispetto ai BST.
    
- **HNSW (Hierarchical Navigable Small World):** Algoritmo che applica il concetto di gerarchia multi-strato non più a liste lineari, ma a grafi di prossimità per ricerche vettoriali complesse.
    
- **Layer / Livelli Gerarchici:** Livelli sovrapposti di dati dove il livello più basso (Layer 0) contiene la struttura completa, mentre i livelli superiori (Layer 1, 2, 3...) contengono versioni drasticamente ridotte per permettere l'attraversamento rapido dei dati.
    
- **Routing Gerarchico:** Il processo greedy per cui una query viene iniziata nel layer più alto (con collegamenti lunghi) per poi scendere verticalmente nei layer più densi man mano che ci si avvicina al target.
---
### kANNolo: Un'Implementazione Efficiente

Nell'ambito delle architetture basate su grafi, si distingue **kANNolo**, un'implementazione scritta in linguaggio Rust dell'algoritmo **HNSW** (Hierarchical Navigable Small World) che si spinge anche oltre le funzioni standard. Una delle caratteristiche fondamentali di questa libreria è di essere l'unica a poter lavorare in modo nativo sia con vettori sparsi (**Sparse**) che densi (**Dense**). Dal punto di vista dello sviluppo, kANNolo è progettato per risultare super efficiente e facile da estendere. Un dettaglio particolare della sua genesi è che il codice è stato implementato prevalentemente da studenti universitari, rendendola di fatto la prima libreria **KNN** (K-Nearest Neighbors) a prendere in prestito il nome da un tipico dolce italiano.

Le eccellenti prestazioni di kANNolo sono supportate dai dati sui collaudi prestazionali. Analizzando i grafici che mettono in relazione le **Queries per second** (Query per secondo) con l'**Accuracy@10** (Accuratezza), è possibile confrontare kANNolo (impostato con parametro $M=32$) con altre librerie molto popolari quali hnswlib, FAISS e N2 (tutte parimenti valutate con $M=32$). Nel grafico, kANNolo si posiziona ai vertici delle prestazioni, mantenendo un alto volume di query al secondo senza sacrificare l'accuratezza, riuscendo a eguagliare o superare le prestazioni dei concorrenti diretti.

![[Pasted image 20260430154104.png]]


### Problemi Aperti degli Approcci basati su Grafi

Nonostante la comprovata velocità in fase di inferenza, gli approcci basati su grafi soffrono di problematiche aperte che ne complicano la gestione su larga scala. Il primo scoglio rilevante è l'**Alto Costo in Tempo per la Costruzione** (High Construction Time). Affinché la topologia sia corretta, per ogni nodo aggiunto è necessario individuare con precisione le connessioni ideali; questo significa che l'operazione di inserimento di un vettore comporta un costo computazionale pari a quello di una vera e propria ricerca. Inoltre, il sistema deve operare in modo tale da assicurare l'assenza di ridondanza durante la creazione di nuovi archi (edges).

Il secondo limite critico è l'**Alto Costo in Spazio** (High Space Usage). Per poter mappare lo spazio, il sistema deve salvare per ogni singolo vettore una grande quantità di archi ad esso associati. Per mantenere inalterata la promessa di un'alta efficienza in fase di ricerca, questi ingombranti indici devono necessariamente risiedere per intero all'interno della memoria RAM.

Infine, l'approccio strutturale genera inefficienze a livello di processore: la ricerca **non è cache-friendly**. Per visitare i vicini di un nodo durante il calcolo, l'algoritmo effettua continui accessi casuali (random accesses) al _forward index_, vanificando i meccanismi di pre-fetching della memoria cache e introducendo colli di bottiglia nei tempi di latenza.

### Product Quantization (Quantizzazione del Prodotto)

Per superare i limiti di memoria e velocizzare ulteriormente il calcolo delle distanze, i sistemi IR impiegano una tecnica nota come **Quantizzazione del Prodotto** (Product Quantization), che si definisce come una strategia di compressione con perdita dei dati (**Lossy Compression**). Il meccanismo logico alla base della quantizzazione consiste nel raggruppare i componenti del vettore originario in un numero $m$ di segmenti (chunks), dove ogni segmento avrà una grandezza pari a $d/m$ componenti. Successivamente, si sostituisce l'intero contenuto di ogni chunk con un codice rappresentativo che occupa un solo byte.

L'applicazione di questa compressione offre tre vantaggi immediati:

1. Riduce drasticamente l'utilizzo dello spazio globale richiesto dal data set.
    
2. Permette un calcolo delle distanze molto più rapido, grazie al numero nettamente inferiore di componenti da elaborare.
    
3. Migliora l'efficienza della memoria, poiché i vettori che devono essere letti e caricati risultano molto più corti. Tuttavia, trattandosi esplicitamente di una compressione lossy, vi è un dazio da pagare: il **Recall** (Richiamo) del sistema può diminuire, e solitamente lo fa.


![[Pasted image 20260430154642.png]]

Per chiarire il concetto con un esempio numerico, prendiamo in esame un vettore originale di dimensione $d=12$, in cui ogni singolo valore è memorizzato come un "float32". Il vettore contiene i seguenti valori: `0.8, -1.2, 3.4, 1.3, -2.1, 0.3, 2.4, 1.5, 0.9, -1.0, 0.8, 4.1`. Seguendo le regole della quantizzazione, il vettore viene frazionato in $m=3$ blocchi, ciascuno contenente 4 valori distinti. A questo punto, si utilizza un singolo byte per codificare per intero ogni blocco, ottenendo in output i valori riassuntivi `3`, `254` e `87`. Il calcolo del risparmio di memoria è immediato: partendo da 12 valori a 32 bit e riducendoli a 3 valori da 8 bit, il sistema ha appena risparmiato l'equivalente di $12 \times 32 - 3 \times 8 = 360$ bit per questo specifico vettore.

### Concetti Chiave

- **kANNolo:** Implementazione in Rust dell'algoritmo HNSW, capace di supportare sia vettori sparsi che densi con alta efficienza, caratterizzata dal nome di un tipico dolce italiano.
    
- **Limitazioni dei Grafi:** Approcci come l'HNSW offrono tempi di ricerca eccellenti ma patiscono alti costi in fase di costruzione dell'indice, eccessivo uso della memoria RAM e scarsa compatibilità con le logiche di caching della CPU.
    
- **Product Quantization (PQ):** Tecnica di compressione lossy che suddivide i vettori ad alta dimensionalità in blocchi più piccoli, codificandoli in singoli byte per risparmiare memoria e accelerare il calcolo delle similarità, a fronte di una potenziale perdita di recall.
---
## Product Quantization: Encoding

L'elaborazione dei dati ad alta dimensionalità richiede tecniche di compressione efficaci, tra cui spicca l'algoritmo discusso in questa sezione. Nello specifico, analizzeremo il processo di codifica associato alla **Product Quantization** (quantizzazione del prodotto).

### La suddivisione dell'Original Vector

Il processo prende in esame un vettore di partenza, definito esplicitamente come **Original vector**, che viene frammentato logica in sotto-vettori più piccoli per poter essere analizzato e compresso. Il primo segmento estratto da questo vettore originale è composto dai valori `0.8, -1.2, 3.4, 1.3`. Questo blocco di dati numerici viene elaborato utilizzando un algoritmo di clustering, nello specifico il **k-means with 256 centroids**.

![[Pasted image 20260430155455.png]]

Attraverso questa operazione di partizionamento dello spazio, il sistema calcola la distanza del sotto-vettore dai vari centroidi di riferimento. Di conseguenza, il blocco `0.8, -1.2, 3.4, 1.3` viene associato e codificato con l'identificativo del suo centroide più vicino, che in questo caso corrisponde al numero **3**.
![[Pasted image 20260430155600.png]]
### La codifica sequenziale dei blocchi successivi

La procedura prosegue in modo iterativo sui frammenti successivi dell'**Original vector**. Il secondo blocco individuato dal sistema contiene i valori `-2.1, 0.3, 2.4, 1.5`. Anche in questo frangente, il sotto-vettore viene sottoposto al calcolo spaziale tramite il medesimo **k-means with 256 centroids**. Il risultato di questa mappatura attribuisce a questo secondo blocco l'identificativo del centroide **254**.
![[Pasted image 20260430155628.png]]

Infine, il sistema analizza il terzo sotto-vettore mostrato, formato dai valori `0.9, -1.0, 0.8, 4.1`. Sfruttando per la terza volta consecutiva il modello **k-means with 256 centroids**, quest'ultima porzione di dati viene matematicamente ricondotta e assegnata al centroide numero **87**.

[![[Pasted image 20260430155649.png]]
### Risultato della Compressione e Perdita di Dati

Al termine di questa rigorosa sequenza di operazioni, l'**Original vector** ad alta dimensionalità risulta interamente codificato in una forma estremamente compatta. Il nuovo array compresso è infatti rappresentato esclusivamente dalla sequenza dei tre centroidi individuati: **3, 254, 87**.

Tuttavia, è fondamentale sottolineare una caratteristica intrinseca e critica di questo metodo matematico: si tratta chiaramente di un processo **Lossy!** (ovvero con perdita di dati). Questo avviene perché l'assegnazione finale a un singolo centroide approssima forzatamente la posizione esatta e originale del vettore nello spazio. In altre parole, l'avvertenza cruciale è che vettori originali differenti potrebbero generare la medesima codifica, poiché tutti i punti vettoriali che ricadono all'interno della stessa macro-regione di spazio delimitata dal k-means verranno irrimediabilmente associati allo stesso identico numero di centroide.

---

### Concetti Chiave

- **Product Quantization**: Tecnica di compressione che codifica un vettore originale frammentandolo in parti più piccole e mappando ogni frammento a un valore rappresentativo.
    
- **k-means with 256 centroids**: Il modello spaziale di clustering utilizzato per dividere lo spazio vettoriale in 256 macro-regioni, ognuna identificata da un centroide numerico.
    
- **Lossy Encoding**: Proprietà della codifica che comporta una perdita di informazione non recuperabile, indicando che vettori di partenza diversi (ma vicini nello spazio) possono produrre esattamente la stessa stringa compressa finale.
---
### Distance Computation nella Product Quantization

Il processo di **Product quantization** si basa in primo luogo sull'operazione di **distance computation**, ovvero il calcolo delle distanze tra un vettore di input e i vettori presenti all'interno del sistema. Partendo da una **Query** specifica, rappresentata da un vettore numerico (con i valori 3.6, 2.1, -0.9, 1.1), il sistema effettua un confronto con altri vettori di riferimento. Nel caso illustrato, i vettori di riferimento con i quali la query interagisce presentano rispettivamente le coordinate 2.1, 2.1, 1.1, 1.4 e 3.1, -1.1, 2.1, 1.1.

Durante questa fase, vengono calcolate le distanze parziali (indicate come **Distances**) strutturando i risultati in apposite matrici o griglie di calcolo. Questo processo iterativo mappa i sottospazi dei vettori per determinare la loro vicinanza spaziale.

![[Pasted image 20260430160543.png]]

Come risultato finale di queste operazioni di mappatura, il sistema identifica un **Document** (documento) specifico, il quale viene rappresentato in forma quantizzata tramite i valori 3, 254 e 87. Le frecce nel processo visivo indicano come specifiche posizioni nelle matrici delle distanze convergano per formare l'identificativo finale del documento.
![[Pasted image 20260430160602.png]]
### Risultati Sperimentali: Recall vs Latency

Per comprendere l'efficacia pratica di questi algoritmi, è fondamentale analizzare gli **Experimental Results** (risultati sperimentali).

L'analisi si concentra sulla relazione "Recall vs Latency (PQ 1M)", ovvero il compromesso tra il livello di richiamo e la latenza del sistema, testato su un milione di vettori estratti dal dataset **Sift**.

![[Pasted image 20260430160629.png]]

Il grafico traccia la metrica del Recall sull'asse verticale (Y), con valori che partono da 0.62 per arrivare al valore massimo di 1, mentre sull'asse orizzontale (X) sono riportati i valori di latenza (o throughput) scanditi in intervalli numerici da 0 fino a 1.800.000. Vengono analizzate le prestazioni di diverse configurazioni del sistema, descritte nella legenda: la curva del **Sift base** e le curve relative alle varianti **Sift (8)**, **Sift (4)**, **Sift (2)** e **Sift (1)**.

Inoltre, sull'asse delle ascisse, poco prima della soglia dei 600.000, è presente una linea verticale di demarcazione associata al termine **Brute Force Scan** (scansione a forza bruta). Questa soglia funge da punto di riferimento per confrontare le prestazioni di efficienza e latenza dei metodi approssimati rispetto a una ricerca lineare ed esaustiva su tutti gli elementi.

---

### Concetti Chiave

- **Product quantization**: Tecnica che permette di rappresentare vettori complessi attraverso componenti quantizzate per ottimizzare lo spazio e i calcoli.
    
- **Distance computation**: L'operazione matriciale attraverso la quale si calcola la vicinanza tra la Query e i vettori presenti nel database.
    
- **Recall vs Latency**: Metrica di valutazione cruciale nei sistemi di Information Retrieval che evidenzia il compromesso (trade-off) tra la qualità dei risultati recuperati e il tempo o le risorse computazionali impiegate per ottenerli.
---
### Le Famiglie di Embedding e la Rappresentazione Sparsa

Nel contesto del recupero neurale delle informazioni, i documenti e le query vengono convertiti in vettori chiamati embedding. Questi si dividono principalmente in tre famiglie: vettori densi singoli (_dense single-vector_), vettori densi multipli (_dense multi-vector_) e vettori sparsi singoli (_sparse single-vector_).

Gli embedding sparsi rappresentano una soluzione di particolare interesse. Essi si distinguono per essere altamente efficaci ed estremamente più efficienti in termini di spazio e tempo rispetto alle controparti dense multi-vettore. Un ulteriore e cruciale vantaggio è la loro interpretabilità "by design", ovvero per concezione nativa.

Per illustrare questo concetto, consideriamo un esempio pratico. Se poniamo al sistema la query _"what shoes do most nba players wear"_ (quali scarpe indossano la maggior parte dei giocatori nba), la sua rappresentazione vettoriale sparsa assegnerà un peso specifico ai termini ritenuti centrali per il contesto. L'algoritmo non si limiterà a pesare i termini esatti, ma dedurrà anche l'importanza semantica, restituendo valori come: _nba_ (2.56), _shoes_ (2.32), _shoe_ (2.14), _basketball_ (1.97), _wearing_ (1.55), _wear_ (1.52), _most_ (1.34) e _players_ (1.25).

### Il Forward Index e la Sfida del Calcolo Approssimato kNN

![[Pasted image 20260430180805.png]]

Quando queste rappresentazioni devono essere applicate su larga scala, il sistema organizza i dati utilizzando un **Forward Index** (Indice Diretto). Questa struttura mappa ogni singolo documento alla lista dei suoi componenti vettoriali. Per esempio, il documento $d_1$ conterrà i componenti associati ai rispettivi valori $c_1 v_1, c_2 v_2, \dots, c_{165} v_{165}$, mentre un documento $d_2$ potrebbe estendersi fino a $c_{183} v_{183}$.

Le dimensioni di questi indici nel mondo reale sono massive: un tipico dataset può comprendere circa **8.8 milioni di documenti** e un vocabolario di circa **30.000 (30K) componenti**. Tuttavia, data la natura "sparsa" di questa tecnica, per un dato documento si registrano mediamente solo **150 componenti non-zero**. L'indice si estende fino a mappare l'ultimo documento, $d_{8M}$, con i suoi relativi componenti (es. fino a $c_{142} v_{142}$).

L'obiettivo fondamentale in fase di ricerca è individuare i top $k$ documenti (ad esempio, ponendo $k=10$) che massimizzano la similarità con la query formulata dall'utente. Se la query è codificata con componenti che vanno da $c_1 v_1$ fino a $c_{43} v_{43}$, il sistema deve calcolare l'argomento massimo del prodotto scalare tra il vettore della query ($q$) e i vettori dei documenti ($v$) appartenenti al dataset ($D$). Questa operazione è formalizzata dalla formula:

$$argmax_{v \in D}^{(k)} q^T v$$

Date le enormi moli di dati, questo calcolo viene svolto in forma **approssimata** (Approximated kNN).
![[Pasted image 20260430181748.png]]

### Innovazioni Recenti: La Competizione Big-ANN

L'importanza della ricerca sui vettori sparsi è culminata nella _NeurIPS'23 Competition Track: Big-ANN_, un evento di riferimento per il settore supportato da giganti tecnologici come Microsoft, Pinecone, AWS e Zilliz. Durante le presentazioni dei vincitori della traccia "Sparse", sono emersi algoritmi di altissimo livello come **PyANNS** (sviluppato da Zihao Wang della Shanghai Jiao Tong University) e **GrassRMA** (sviluppato da Meng Chen, Yue et al.).

L'evidenza empirica più importante emersa dalla competizione, i cui dettagli sono disponibili su _[https://big-ann-benchmarks.com/neurips23.html](https://big-ann-benchmarks.com/neurips23.html)_, è che **le soluzioni basate su grafi (Graph-based solutions) superano quelle basate su Indici Invertiti con un margine davvero molto ampio**.
![[Pasted image 20260430181911.png]]

### Gli Indici Invertiti e l'Inefficienza del Brute Force

Nonostante il successo dei grafi, gli **Inverted Indexes** restano fondamentali e ampiamente studiati. Un Indice Invertito capovolge la logica del Forward Index: per ogni componente o termine del vocabolario (da $c_1$ a $c_{30K}$), elenca tutti i documenti in cui esso compare.

- Il termine $c_1$ può essere presente nei documenti $d_1, d_2, d_3, d_4, \dots, d_{1,650,000}$.
    
- Il termine $c_2$ può comparire in $d_1, d_2, d_3, d_4, \dots, d_{873,066}$.
    
- Il termine $c_{30K}$ si trova in $d_1, d_2, d_3, d_4, \dots, d_{581,345}$.
    

![[Pasted image 20260430182147.png]]


Se si applicasse un approccio di tipo **Brute Force** per valutare la nostra query contro gli 8.8 milioni di documenti (sia sul Forward che sull'Inverted Index), il sistema sarebbe costretto a eseguire ben **8.841.823 calcoli di prodotto scalare** (dot product computations). Durante l'elaborazione, le architetture fanno uso di una struttura ad albero chiamata **Heap** per immagazzinare e ordinare i documenti più promettenti man mano che vengono valutati (es. nodi 1, 2, 3, 4, 5, 9). Per limitare questo onere computazionale, la letteratura suggerisce tecniche come **IOQP** (basata su liste ordinate per impatto e terminazione anticipata) o **SparseIVF** (basata su indici invertiti di file e sketches).
![[Pasted image 20260430182339.png]]

### Il Metodo Seismic: Pruning e la Concentrazione dell'Importanza

L'ottimizzazione decisiva per il Retrieval su vettori sparsi è introdotta dal metodo **Seismic**, basato sul principio della **Concentrazione dell'Importanza (Concentration of Importance)**.

![[Pasted image 20260430182430.png]]

Il principio matematico di Seismic rileva che, nei vettori sparsi, il **90% del prodotto scalare viene generato avvalendosi solo del ~15% dei termini**. Seismic sfrutta questa concentrazione per applicare una **potatura (pruning)** su due direttrici, applicata per esempio a una query complessa estesa fino a $c_{112} v_{112}$:

1. **Limitazione dei Documenti ($\lambda$):** Il sistema memorizza o analizza solo i top-$\lambda$ documenti all'interno di ogni lista dell'indice. Scegliendo, ad esempio, di conservare solo **$\lambda=4000$** documenti per lista, il volume delle operazioni matematiche crolla immediatamente dagli 8.841.823 iniziali a circa **4.000.000**.
    
2. **Limitazione dei Componenti della Query ($\sigma$):** Oltre a ridurre i documenti, l'algoritmo analizza unicamente le liste relative ai componenti principali della query. Scegliendo di valutare solo i top-$\sigma$ termini della query (es. **$\sigma=10$**), il numero totale dei calcoli di prodotto scalare viene abbattuto a sole **40.000 computazioni**.

### Validazione e Accuratezza


Questa potatura massiccia deve essere validata per assicurarsi di non perdere i documenti realmente utili. Si utilizza quindi la metrica **Accuracy@10**, definita come la frazione dei reali 10 migliori vettori che il sistema riesce effettivamente a recuperare.

Applicando la regola del $\lambda=4000$, i dati dimostrano la robustezza del sistema:

![[Pasted image 20260430182739.png]]

I risultati evidenziano chiaramente che, limitando l'analisi a soli 10 termini della query, il sistema Seismic riesce a raggiungere un'accuratezza superiore al 98%, offrendo prestazioni straordinarie e validando a pieno il concetto di concentrazione dell'importanza.

---

### Glossario e Concetti Chiave

- **Sparse Embeddings:** Rappresentazione matematica in cui documenti e query sono tradotti in vettori composti prevalentemente da valori nulli. Si rivelano altamente efficienti in termini di spazio/tempo e la loro struttura pesata li rende nativamente interpretabili.
    
- **Indici Forward e Inverted:** Il Forward Index elenca per ogni documento tutti i componenti in esso presenti. Al contrario, l'Inverted Index associa a ciascun componente del vocabolario la lista dei documenti che lo contengono.
    
- **Concentration of Importance:** Regola statistico-matematica chiave nei vettori sparsi, la quale dimostra che circa il 15% dei termini è responsabile della genesi del 90% del prodotto scalare complessivo.
    
- **Pruning (Seismic):** Strategia di ottimizzazione che abbatte i costi computazionali tagliando i dati da analizzare. Considerando solo $\lambda=4000$ documenti per lista e $\sigma=10$ termini per query, le computazioni calano da quasi 9 milioni a sole 40.000, mantenendo un'Accuracy@10 del 98.30%.

---
### Architettura di Base e la Tecnica del Pruning

L'infrastruttura di base si fonda su due strutture dati fondamentali: l'**Inverted Index** e il **Forward Index**. L'indice invertito mappa una vasta gamma di concetti (da $C_{1}$ fino a $C_{30K}$) alle relative liste di documenti associati (come $d_{1} \dots d_{4000}$). Parallelamente, il Forward Index collega i documenti (fino a $d_{8M}$) alle coppie concetto-valore, come ad esempio $C_{1} V_{1}$ e $C_{2} V_{2}$.

![[Pasted image 20260430184632.png]]

Quando viene processata una query composta da vari termini (ad esempio da $C_{1} V_{1}$ fino a $C_{43} V_{43}$), il sistema deve eseguire il calcolo del prodotto scalare (dot product computations) per stimare la rilevanza. Valutare questi punteggi con un approccio **Brute Force** richiederebbe ben 8.841.823 computazioni. Affidandosi semplicemente all'**Inverted Index**, il numero scende a circa 4.000.000 di calcoli. Per ottimizzare ulteriormente questo processo, Seismic introduce il **Pruning**, riducendo le computazioni a circa 40.000.

Il pruning opera attraverso l'utilizzo di una struttura dati **Heap** che mantiene una soglia di riferimento definita $\tau$. Questa soglia rappresenta il punteggio minimo (minimum score) necessario affinché un documento candidato possa entrare nell'heap dei risultati. Nonostante questa notevole scrematura, emerge una forte inefficienza operativa: il sistema sta ancora valutando tutti i documenti presenti nella lista candidata ("We are evaluating all the documents in the list!").

]

### Ottimizzazione tramite Blocking

Per superare il limite della valutazione esaustiva, Seismic implementa la strategia del **Blocking**. L'obiettivo principale diventa quindi quello di saltare i documenti irrilevanti senza dover minimamente calcolare il loro prodotto scalare con la query.

Questo risultato si ottiene permutando e raggruppando i documenti all'interno di ogni posting list in base alla loro similarità. Questa operazione di raggruppamento viene effettuata applicando una versione superficiale (shallow version) dell'algoritmo di clustering **K-Means**. Di conseguenza, si vengono a creare dei blocchi (etichettati come $S_{1}, S_{2} \dots S_{\beta}$) contenenti ciascuno decine di documenti simili tra loro. Il vantaggio di questa architettura è evidente: diventa possibile saltare un intero blocco di documenti se si ha la certezza che non contenga alcun documento rilevante per la query.

### Creazione e Raffinamento dei Summaries

Per poter scartare un intero blocco senza doverlo scansionare, Seismic utilizza i **Summaries** (sommari), i quali hanno lo scopo specifico di stimare il prodotto scalare del miglior documento presente all'interno del blocco stesso.

La costruzione del summary avviene partendo dall'analisi dei vettori dei singoli documenti (come $d_{1}, d_{2}, d_{3}$) e calcolando il valore massimo per ogni singola componente. Questo primo approccio risulta essere **conservativo**, poiché stabilisce un limite superiore (upper bounds) certo per il vero prodotto scalare. Tuttavia, dal punto di vista pratico, questa tecnica genera un sommario con troppe componenti diverse da zero, andando a pesare sulle prestazioni.

![[Pasted image 20260430184957.png]]
La soluzione adottata consiste nel raffinare il summary mantenendo esclusivamente le componenti non-zero più grandi e scartando le altre (ad esempio, eliminando valori minori in favore dei picchi massimi come 3.1 e 3.5). Applicando questo taglio, il sommario diventa **preciso anche se non più conservativo**, garantendo parallelamente un uso ragionevole dello spazio di memoria ("Reasonable space usage").


### Impatto Computazionale Finale

L'integrazione di queste tecniche modifica drasticamente le prestazioni del sistema. Aggiungendo il **Blocking** e l'uso dei sommari raffinati, il numero di computazioni necessarie per il calcolo del prodotto scalare crolla ulteriormente a circa 5.100 operazioni. Tali metodologie rappresentano il nucleo di Seismic, a cui si aggiungono molte altre ottimizzazioni architetturali ("and many other optimizations!") per rendere il recupero delle informazioni estremamente reattivo.

|**Metodo**|**# Dot Product Computations**|
|---|---|
|**Brute Force**|8.841.823|
|**Inverted Index**|~4.000.000|
|**Pruning**|~40.000|
|**Blocking**|~5.100|

![[Pasted image 20260430185045.png]]

---

### Concetti Chiave

- **Pruning tramite Heap:** Eliminazione dei documenti candidati che non raggiungono la soglia minima ($\tau$) definita dall'Heap, riducendo le computazioni a circa quarantamila.
    
- **Blocking (Shallow K-Means):** Raggruppamento di decine di documenti simili all'interno delle posting list, permettendo di ignorare blocchi interi senza calcolare il prodotto scalare se ritenuti irrilevanti.
    
- **Summaries Ottimizzati:** Vettori che stimano il punteggio massimo di un blocco calcolando il valore massimo per componente, resi efficienti per lo spazio mantenendo solo le componenti non-zero maggiori a discapito della conservatività.

---
![[Pasted image 20260430185057.png]]
![[Pasted image 20260430185105.png]]
![[Pasted image 20260430185116.png]]
![[Pasted image 20260430185127.png]]