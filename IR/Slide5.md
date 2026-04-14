# Slide 5: Compressione di Dati nei Motori di Ricerca Moderni

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

## Codifiche Prefix-Free e Compressione di Liste

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
