# Slide 9: Addestramento e Inferenza Efficienti di Ensemble di Alberi di Decisione

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
