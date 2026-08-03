# Information Retrieval — Appunti completi del corso

> Argomenti trattati: Introduzione all'IR · Valutazione · Text Processing ·
> Modello Booleano e VSM · TF-IDF e BM25 · Indici invertiti ·
> Query processing e top-k · Compressione dati · Learning to Rank ·
> Alberi di decisione · Neural IR · Approximate Nearest Neighbor

---
# Introduzione all'Information Retrieval

## Anatomia di un motore di ricerca

Un moderno sistema di Information Retrieval (IR) è strutturato in due macro-ambiti operativi distinti ma interdipendenti: la fase **offline** e la fase **online**.

La **fase offline** gestisce l'acquisizione e l'organizzazione dei dati prima che avvenga qualsiasi interazione con l'utente:
- **Document Collection**: raccolta dei documenti.
- **Indexing**: trasforma i dati grezzi in strutture efficienti come l'*indice invertito* (Inverted Index), che mappa i termini ai documenti che li contengono.
- **Feature Processor**: estrae e processa le caratteristiche dei documenti, archiviate in un *Document Features Repository*.
- **Learning-to-rank Model Training**: addestra, tramite dati di training specifici, una *Learned Ranking Function* capace di distinguere la rilevanza dei risultati.

La **fase online** si attiva quando l'utente invia una query:
1. La query viene elaborata ed eventualmente espansa (*Expanded Query*) per migliorarne precisione o copertura.
2. Il modulo di **Query Processing** interroga l'indice invertito per recuperare un insieme preliminare di documenti candidati.
3. Il sistema recupera le feature calcolate offline e applica la funzione di ranking appresa per ordinare i risultati finali secondo la rilevanza stimata.

![Architettura di un motore di ricerca: fasi offline (indicizzazione, feature processing, training) e online (query processing, scoring, ranking).](Files%20and%20Images/fig_architettura_ir.png)

*Architettura di un motore di ricerca: fasi offline (indicizzazione, feature processing, training) e online (query processing, scoring, ranking).*

## Efficacia ed Efficienza

La progettazione e la valutazione di un sistema IR sono guidate da due obiettivi primari, spesso in competizione tra loro.

> **Definizione: Efficienza (Efficiency)**
>
> Misura quantitativa relativa all'utilizzo delle risorse di calcolo e alla velocità del sistema. Si declina in:
> - **Velocità di indicizzazione**: documenti processati per unità di tempo (es. indicizzazione incrementale).
> - **Velocità di ricerca**: per la ricerca online le metriche critiche sono la *latenza* (tempo per processare una query) e il *throughput* (volume di query gestibili simultaneamente).
> - Ottimizzazione di CPU, gerarchia di memoria, calcolo parallelo e compressione dei dati.
>

> **Definizione: Efficacia (Effectiveness)**
>
> Misura la qualità intrinseca dei risultati restituiti, ovvero la capacità del sistema di soddisfare il bisogno informativo dell'utente. Un sistema efficace restituisce documenti pertinenti (*Relevant*) e riduce il rumore informativo (*Nonrelevant*). Richiede benchmark composti da collezioni di documenti, query e giudizi di rilevanza. Le metriche principali sono Precisione, Recall, **MAP** e **NDCG** (Capitolo *Valutazione dei Sistemi di IR*).

Efficienza ed efficacia sono spesso in tensione: un modello più sofisticato (più efficace) richiede tipicamente più tempo di calcolo (meno efficiente). Gran parte del corso  –  dalla potatura dinamica alla compressione, dalle pipeline multi-stage alla quantizzazione vettoriale  –  riguarda proprio tecniche per ottenere la massima efficacia al minor costo computazionale possibile.

# Valutazione dei Sistemi di IR

## Metodologia Cranfield e Benchmark

La felicità dell'utente è difficile da misurare direttamente; l'IR usa quindi la **rilevanza** dei risultati come proxy principale. Questo approccio sperimentale nasce dagli **esperimenti di Cranfield** di Cyril Cleverdon, che hanno stabilito il paradigma standard di valutazione.

> **Definizione: Benchmark**
>
> Un benchmark standardizzato per valutare un sistema di IR è composto da tre elementi:
> 1. Una **collezione di documenti** di riferimento.
> 2. Una **suite di query** (rappresentano i bisogni informativi).
> 3. Un insieme di **giudizi di rilevanza** (relevance judgments): valutazione esplicita se un documento è *Relevant* o *Nonrelevant* per una data query.
>

### TREC e la tecnica del Pooling

La **Text REtrieval Conference** (TREC), sponsorizzata dal NIST, fornisce l'infrastruttura per test su larga scala delle tecnologie IR. Le attività sono organizzate in *track* (aree tematiche che astraggono task utente reali). Il risultato prodotto da un sistema su una collezione di test è detto *run*.

Poiché giudicare manualmente la rilevanza di ogni documento per ogni query in collezioni vaste (milioni di documenti) è impraticabile, si usa il **Pooling**: si raccolgono i top results di diverse run e si combinano in un pool; solo i documenti nel pool vengono giudicati da assessori umani. I documenti *unpooled* (non giudicati) vengono assunti non rilevanti in fase di valutazione.

## Metriche non ponderate (Unranked Measures)

Con giudizi di rilevanza binari si usano metriche che valutano l'insieme dei documenti recuperati senza considerarne l'ordine.

> **Definizione: Precision e Recall**
>
> $$
> P = \frac{tp}{tp+fp} = \frac{|Rel \cap Ret|}{|Ret|} \qquad\qquad R = \frac{tp}{tp+fn} = \frac{|Rel \cap Ret|}{|Rel|}
> $$
>
> La *Precision* è la frazione di documenti recuperati che sono rilevanti; il *Recall* è la frazione di documenti rilevanti totali che sono stati recuperati.

Poiché esiste un trade-off tra le due misure, si usa la **F-Measure**, media armonica di precisione e recall:

$$
F = \frac{2PR}{P+R}, \qquad F_\beta = \frac{(1+\beta^2)PR}{\beta^2 P + R}
$$

La media armonica è preferita a quella aritmetica perché penalizza fortemente i valori molto bassi di una delle due componenti (resta vicina al minimo tra i due valori). Con $\beta < 1$ si dà più peso alla precisione, con $\beta > 1$ al recall.

## Metriche basate sul Rank

Nei sistemi reali l'ordine di presentazione è cruciale e le metriche unranked non bastano.

> **Definizione: Precision@K**
>
> La **P@K** calcola la percentuale di documenti rilevanti tra i primi $K$ risultati, ignorando tutto oltre il rank $K$. È fondamentale nella ricerca web, dove l'utente esamina solo i primi risultati.

> **Definizione: Mean Average Precision (MAP)**
>
> La **Average Precision** (AP) per una singola query è la media dei valori di precisione calcolati in corrispondenza di ogni documento rilevante recuperato nella lista ordinata. La **MAP** è la media aritmetica (macro-averaging) degli AP su un insieme di query. A differenza di P@K, l'AP considera tutte le posizioni dei documenti rilevanti.

> **Definizione: Mean Reciprocal Rank (MRR)**
>
> In scenari come il *known-item search* o le query navigazionali (l'utente cerca un'unica risposta corretta), si usa l'**MRR**: il reciproco della posizione del primo documento rilevante trovato, mediato sulle query. Riflette lo sforzo dell'utente nel trovare la risposta corretta.
>
> $$
> MRR = \frac{1}{|Q|}\sum_{i=1}^{|Q|} \frac{1}{rank_i}
> $$
>

## Rilevanza Graduata: CG, DCG e NDCG

Le metriche binarie non catturano le sfumature di utilità tra documenti diversi (un documento può essere parzialmente rilevante, molto rilevante o totalmente inutile). Si adottano quindi metriche a **rilevanza graduata** (es. scala 0–4), basate su due assunzioni: i documenti molto rilevanti sono più utili di quelli marginalmente rilevanti, e la loro utilità decresce se appaiono in posizioni basse della classifica.

> **Definizione: Cumulative Gain (CG)**
>
> Il **CG** è la metrica più semplice per la rilevanza graduata: somma i punteggi di rilevanza $rel_i$ dei documenti fino al rango $p$, senza considerarne l'ordine:
>
> $$
> CG_p = \sum_{i=1}^{p} rel_i
> $$
>
> Limite critico: scambiare un documento molto rilevante in posizione 1 con uno poco rilevante in posizione $p$ non altera $CG_p$. Questo contraddice l'esperienza utente, dove l'attenzione è massima sui primi risultati.

> **Definizione: Discounted Cumulative Gain (DCG)**
>
> Per penalizzare i documenti rilevanti posizionati in basso, il **DCG** applica uno sconto logaritmico basato sul rango:
>
> $$
> DCG_p = rel_1 + \sum_{i=2}^{p} \frac{rel_i}{\log_2(i)}
> $$
>
> Una formulazione alternativa, usata spesso dai motori di ricerca, enfatizza maggiormente i documenti altamente rilevanti:
>
> $$
> DCG_p = \sum_{i=1}^{p} \frac{2^{rel_i}-1}{\log_2(i+1)}
> $$
>

> **Definizione: Normalized DCG (NDCG)**
>
> Il DCG non è confrontabile tra query diverse, perché dipende dal numero di documenti rilevanti disponibili per ciascuna query. Si normalizza quindi rispetto all'**Ideal DCG (IDCG)**, il valore massimo di DCG ottenibile ordinando i documenti in modo perfetto per rilevanza decrescente:
>
> $$
> NDCG_p = \frac{DCG_p}{IDCG_p} \in [0,1]
> $$
>
> $NDCG_p = 1$ indica un ordinamento perfetto fino alla posizione $p$.

> **Osservazione: perché metriche diverse appartengono a filoni diversi della letteratura**
>
> Precision e Recall derivano dal recupero di informazioni classico (biblioteche, basi dati legali), dove l'obiettivo è spesso la completezza o l'accuratezza pura su insiemi *non ordinati* e con rilevanza binaria. DCG e NDCG appartengono invece alla letteratura moderna del Web Search e del Learning-to-Rank, dove l'attenzione è sull'esperienza utente che interagisce quasi esclusivamente con i primi risultati (top-k). La differenza sostanziale è duplice: la **sensibilità alla posizione** (ranking-awareness, assente in P/R, centrale in DCG/NDCG) e la **granularità del giudizio di rilevanza** (binaria vs. graduata).

# Proprietà del Linguaggio Naturale e Text Processing

## Caratteristiche del linguaggio naturale

Il linguaggio naturale si distingue nettamente dai linguaggi formali (di programmazione) per la sua natura intrinsecamente disordinata e complessa. La struttura è governata dalla **sintassi** (regole per combinare parole in frasi corrette), ma la correttezza sintattica non garantisce il senso: entra in gioco la **semantica composizionale**, per cui il significato di una frase complessa deriva dalla combinazione dei significati delle parti costituenti.

L'ostacolo principale nell'elaborazione automatica è l'**ambiguità**, che permea ogni livello linguistico:
- *Lessicale* (polisemia): una parola con più significati (es. “pesca”).
- *Sintattica*: strutture interpretabili in modi diversi.
- *Semantica*.

Il linguaggio è inoltre fortemente **dinamico**: nascono neologismi, le regole si adattano all'uso (slang), i significati mutano nel tempo  –  rendendo difficile mantenere modelli statici sempre validi.

## Natural Language Understanding (NLU)

Il **NLU** si occupa della comprensione profonda del significato del testo. I problemi di NLU sono spesso classificati **AI-complete** (o AI-hard): risolverli pienamente equivarrebbe a risolvere il problema centrale dell'AI Generale, richiedendo conoscenza del mondo e ragionamento contestuale, non solo capacità linguistiche.

Applicazioni pratiche abilitate dall'NLU: classificazione automatica dei testi, estrazione di informazioni strutturate, *Question Answering*, *Text Summarization*, *Machine Translation*.

## Pipeline di elaborazione del testo

> **Definizione: Tokenizzazione e Sentence Splitting**
>
> La **Tokenizzazione** segmenta il flusso di caratteri in *token* (parole, numeri). Presenta complessità legate a punteggiatura, acronimi, caratteri speciali e lingue senza spazi separatori (es. cinese). Il **Sentence Splitting** individua i confini delle frasi, distinguendo un punto fermo di fine frase da un punto in un'abbreviazione (es. “ecc.”).

Per ridurre la variabilità del vocabolario e mappare varianti morfologiche allo stesso concetto:

> **Definizione: Stemming vs Lemmatizzazione**
>
> - **Stemming**: processo euristico e grezzo che rimuove i suffissi per ottenere la radice (stem). Algoritmi come il *Porter Stemmer* usano regole di troncamento; leggero computazionalmente ma soggetto a errori di *over-stemming* (unifica parole con significati diversi) o *under-stemming*.
> - **Lemmatizzazione**: processo più raffinato, richiede analisi morfologica completa (spesso con dizionari), riconducendo ogni parola al suo *lemma* (es. “sono” $\to$ “essere”). Tiene conto del contesto e della Part-of-Speech, garantendo maggiore precisione a fronte di un costo computazionale più elevato.
>

## Strumenti Python per il Text Processing

- **NLTK** (Natural Language Toolkit): suite di riferimento per didattica/prototipazione in NLP (tokenizzazione, stemming, lemmatizzazione, accesso a corpora).
- **urllib**: modulo standard per richieste HTTP e download di risorse web.
- **BeautifulSoup**: libreria per il parsing di HTML/XML; permette di navigare il DOM, pulire il “tag soup” ed estrarre il contenuto testuale rilevante scartando i tag di formattazione.

# Rappresentazione del Testo e Modello Booleano

## Bag of Words e N-grammi

> **Definizione: Bag of Words (BOW)**
>
> Un documento è rappresentato come un “sacco” di parole: si ignora completamente struttura grammaticale, sintassi e ordine dei termini. L'unica informazione preservata è la molteplicità (quante volte un termine appare). Nonostante la semplificazione, il BOW è spesso sufficiente per classificazione e retrieval di base, poiché il lessico usato è un forte indicatore dell'argomento trattato.

> **Definizione: N-grammi**
>
> Un N-gramma è una sequenza contigua di $N$ elementi estratta da un testo. Con elementi-parola, gli N-grammi catturano frasi o espressioni idiomatiche.

> **Esempio: bi-grammi di parole**
>
> La frase “il cielo è blu” genera i bi-grammi ($N=2$): `"il cielo"`, `"cielo è"`, `"è blu"`.

Esistono anche N-grammi di *caratteri*, utili per gestire errori di battitura (spelling correction) o lingue morfologicamente ricche, permettendo di identificare radici o suffissi comuni. All'aumentare di $N$ il vocabolario tende però a esplodere, aumentando la sparsità dei dati.

## Legge di Zipf e Stopwords

> **Definizione: Legge di Zipf**
>
> Dato un corpus di linguaggio naturale, la frequenza $f$ di una parola è inversamente proporzionale al suo rango $r$ (posizione nella classifica delle parole più frequenti):
>
> $$
> f(r) \propto \frac{1}{r^\alpha}, \qquad \alpha \approx 1
> $$
>
> Ne consegue che poche parole sono estremamente frequenti, mentre esiste una lunghissima coda di parole rare.

Le parole ad altissima frequenza (prime posizioni del rango) sono generalmente termini funzionali (articoli, preposizioni, congiunzioni: in inglese *the, a, to, of*), dette **Stopwords**. Poiché appaiono in quasi tutti i documenti, hanno potere discriminante quasi nullo: è pratica comune rimuoverle in indicizzazione, per ridurre dimensione dell'indice e rumore semantico  –  sebbene in contesti come la ricerca di frasi esatte la loro presenza possa essere necessaria.

> **Osservazione: l'estremo opposto della coda di Zipf – feature rare**
>
> All'estremità opposta rispetto alle stopword si trovano le **feature rare**: termini con frequenza assoluta o document frequency molto bassa (*hapax legomena*, refusi di battitura, identificativi artificiali come slug). Non generalizzano bene: se un termine è comparso una sola volta in passato, è improbabile che aiuti a processare testo futuro. Filtrarli riduce la dimensione del vocabolario e velocizza indicizzazione e ricerca, con impatto tipicamente contenuto sull'efficacia.

## Vector Space Model (VSM)

Per applicare l'algebra lineare all'IR, documenti e query sono rappresentati come vettori in uno spazio euclideo ad alta dimensionalità, dove ogni dimensione corrisponde a un termine distinto del vocabolario $V$ (se $|V|$ termini, lo spazio ha $|V|$ dimensioni).

Un documento $d_j$ è rappresentato come $\vec{d_j} = (w_{1,j}, \dots, w_{|V|,j})$, dove $w_{i,j}$ è il peso del termine $i$ nel documento $j$ (binario o basato sulla frequenza).

> **Osservazione: sparsità**
>
> Un singolo documento contiene solo una piccolissima frazione dei termini totali del vocabolario: la stragrande maggioranza delle componenti del vettore è pari a zero. Questa sparsità è sfruttata computazionalmente con strutture dati efficienti come le *Inverted Lists* (Capitolo *Indici Inversi e Query Processing*).

## Retrieval Booleano

Il **Retrieval Booleano** è il modello più antico, basato su teoria degli insiemi e logica esatta. Le query combinano termini con operatori **AND**, **OR**, **NOT**:
- **AND** (intersezione): documenti che contengono *tutti* i termini (riduce il set di risultati).
- **OR** (unione): documenti che contengono *almeno uno* dei termini (espande il set).
- **NOT** (differenza): esclude i documenti che contengono il termine.

> **Osservazione: il problema del “feast or famine”**
>
> Non prevedendo un ranking (solo decisione binaria rilevante/non rilevante), l'utente si trova spesso di fronte a due estremi:
> 1. Query generiche (spesso con OR) restituiscono un numero enorme di documenti (*feast*), impossibile da analizzare manualmente.
> 2. Query troppo specifiche (spesso con molti AND) non restituiscono alcun risultato (*famine*), perché nessun documento soddisfa tutte le condizioni.
>

## Coefficiente di Jaccard

Per superare la limitazione binaria e introdurre un primo ordinamento per similarità:

> **Definizione: Coefficiente di Jaccard**
>
> Misura la sovrapposizione tra l'insieme dei termini della query $A$ e l'insieme dei termini del documento $B$:
>
> $$
> J(A,B) = \frac{|A \cap B|}{|A \cup B|} \in [0,1]
> $$
>

Limiti: non considera la frequenza dei termini (una parola con 1 occorrenza conta come una con 10), non tiene conto della rarità dei termini nella collezione (una stopword in comune pesa come un termine tecnico raro), e tratta i documenti come sacchi di parole binari senza pesi.

> **Esempio: un caso in cui Jaccard ordina in modo controintuitivo**
>
> Query $q=$ “ides of march”; D1 $=$ “caesar died in march”; D2 $=$ “the long march”. L'unico termine condiviso da $q$ è “march” in entrambi i casi, ma $J(q,D1) = 1/(3+4-1) = 1/6 \approx 0.17$ mentre $J(q,D2) = 1/(3+3-1) = 1/5 = 0.20$: secondo Jaccard, D2 è più simile a $q$ di D1. Il risultato sorprende (D1 sembra tematicamente più vicino), ma il problema non è un errore di calcolo: è che D2, essendo più corto, ha un'unione più piccola con la query, e quindi un punteggio Jaccard più alto a parità di intersezione. Questo evidenzia proprio i limiti elencati sopra – nessuno dei due documenti ripete termini, quindi qui il difetto principale non è la mancata gestione del tf, ma la sensibilità di Jaccard alla sola dimensione degli insiemi coinvolti, indipendentemente da quanto i termini condivisi siano informativi.

# Ponderazione (Weighting) e BM25

## Term Frequency e Log-frequency Weighting

La frequenza grezza $tf_{t,d}$ (numero di occorrenze del termine $t$ nel documento $d$) non è un buon indicatore diretto di rilevanza: un termine che appare 10 volte non rende il documento 10 volte più rilevante di una singola occorrenza (la rilevanza non cresce linearmente con la frequenza).

> **Definizione: Log-frequency weighting**
>
> $$
> w_{t,d} = \begin{cases} 1 + \log_{10}(tf_{t,d}) & \text{se } tf_{t,d} > 0 \\ 0 & \text{altrimenti} \end{cases}
> $$
>
> Il peso cresce molto più lentamente all'aumentare delle occorrenze: passando da 1 a 1000 occorrenze, il peso passa solo da 1 a 4.

## Inverse Document Frequency (idf)

Non tutti i termini hanno lo stesso contenuto informativo: termini comuni (“il”, “essere”) hanno potere discriminante nullo, termini rari (es. “aracnocentrico”) caratterizzano fortemente il documento.

> **Definizione: idf**
>
> Con $df_t$ = numero di documenti contenenti $t$, e $N$ = numero totale di documenti:
>
> $$
> idf_t = \log_{10}\left(\frac{N}{df_t}\right)
> $$
>
> L'idf di un termine raro è alto; quello di un termine molto frequente è vicino a 0. È una misura *globale*, unica per termine, indipendente dal singolo documento.

> **Definizione: Collection Frequency (cf) vs Document Frequency (df)**
>
> La **collection frequency** $cf_t$ è il numero totale di occorrenze di $t$ in tutta la collezione (somma su tutti i documenti, conta anche le ripetizioni); è un indice di popolarità globale del termine. Per il ranking classico il segnale chiave resta però $df_t$ (in quanti documenti distinti compare $t$), non $cf_t$: un termine può avere $cf_t$ alto perché si ripete molto in pochi documenti, pur avendo $df_t$ basso (e quindi restando un buon discriminante).

> **Osservazione: perché l'idf da solo non ordina query a termine singolo**
>
> Se $q=\{t\}$, l'idf è identico per tutti i documenti che contengono $t$: da solo non induce alcun ordinamento tra loro. Serve combinarlo con un segnale locale (tf) o con altri termini di query per ottenere un ranking non banale.

## Schema TF-IDF

$$
w_{t,d} = (1+\log(tf_{t,d})) \times \log\left(\frac{N}{df_t}\right)
$$

Assegna peso elevato ai termini frequenti in un documento specifico (alto tf) ma rari nel resto della collezione (alto idf). Esistono diverse varianti di tf, idf e normalizzazione; la **notazione SMART** le identifica con tre lettere per documento e tre per query (es. `lnc.ltc`): fattore di frequenza del termine (es. `l` = logaritmico), fattore di document frequency (es. `t` = idf, `n` = nullo), fattore di normalizzazione (es. `c` = coseno).

> **Definizione: Normalizzazione Maximum TF (Augmented TF)**
>
> Alternativa al log-weighting per attenuare il bias dovuto alla lunghezza del documento: si riscala $tf_{t,d}$ rispetto alla frequenza massima nello stesso documento, $\max tf_d = \max_{\tau\in d} tf_{\tau,d}$:
>
> $$
> ntf_{t,d} = \begin{cases} a + (1-a)\dfrac{tf_{t,d}}{\max tf_d} & \text{se } tf_{t,d}>0 \\ 0 & \text{altrimenti}\end{cases}
> $$
>
> con $a\in[0,1)$, tipicamente $a=0.5$ (schema `a` della notazione SMART). Mitiga l'aumento di tf dovuto solo alla ripetizione in documenti lunghi, complementare alla normalizzazione coseno; è però sensibile alla presenza di un singolo termine outlier molto ripetuto.

## Distanza Euclidea vs Cosine Similarity

La **Distanza Euclidea** tra vettori documento/query è problematica in IR: è sensibile alla lunghezza del documento (un documento che ripete il contenuto della query molte volte ha un vettore a grande magnitudine, risultando “distante” anche se altamente rilevante).

> **Definizione: Cosine Similarity**
>
> $$
> \text{sim}(\vec{d},\vec{q}) = \frac{\vec{d}\cdot\vec{q}}{|\vec{d}||\vec{q}|}
> $$
>
> Normalizzando i vettori (vettori unitari), la lunghezza del documento non influenza più il punteggio: conta solo l'orientamento (distribuzione relativa dei termini). Un documento lungo e uno breve con la stessa distribuzione di termini hanno cosine similarity pari a 1.

## Best Match 25 (BM25)

Il **BM25** è una funzione di ranking probabilistica, stato dell'arte tra i metodi basati sulla frequenza dei termini prima dei modelli neurali. Introduce due meccanismi che risolvono i limiti del tf-idf classico.

> **Definizione: Saturazione della frequenza**
>
> Nel tf-idf il peso logaritmico continua a crescere indefinitamente con tf. Nel BM25, l'influenza del termine si avvicina asintoticamente a un valore massimo controllato dal parametro $k_1$: oltre una certa frequenza, ulteriori occorrenze hanno impatto minimo sul punteggio.

> **Definizione: Normalizzazione della lunghezza**
>
> I documenti lunghi non sono penalizzati indiscriminatamente (potrebbero essere lunghi perché ricchi di contenuti diversi); vengono confrontati con la lunghezza media della collezione $avgdl$. Il parametro $b$ controlla l'intensità: $b=0$ nessuna normalizzazione, $b=1$ normalizzazione completa.

> **Definizione: Formula BM25 (RSV)**
>
> $$
> RSV_d = \sum_{t \in Q} IDF_t \cdot \frac{tf_{t,d}\cdot(k_1+1)}{tf_{t,d}+k_1\cdot\left(1-b+b\cdot\frac{|d|}{avgdl}\right)}
> $$
>
> con $k_1 \in [1.2, 2.0]$ tipicamente, e $b = 0.75$ solitamente.

> **Osservazione: BM25 come evoluzione del TF-IDF**
>
> Il BM25 è considerato l'evoluzione fondamentale del TF-IDF perché ne risolve due limitazioni concrete: (1) nel TF-IDF il punteggio cresce (log-)linearmente con tf senza mai saturare, mentre nel BM25 il contributo si stabilizza asintoticamente evitando che documenti ripetitivi dominino la classifica; (2) il TF-IDF (con normalizzazione coseno) non gestisce in modo mirato la lunghezza del documento rispetto alla media della collezione, mentre BM25 introduce il parametro $b$ per calibrare esplicitamente questa normalizzazione. Il BM25 rimane oggi la baseline lessicale di riferimento anche nei sistemi neurali ibridi.

## Esempio completo (Toy Dataset)

Collezione di 5 documenti, vocabolario ridotto {gatto, cane, topo}:
- **D1**: “il gatto insegue il cane”
- **D2**: “gatto gatto gatto”
- **D3**: “il cane dorme”
- **D4**: “il topo mangia”
- **D5**: “il gatto e il topo”

Query $q$: “gatto cane”.

### Binary Term-Document Incidence Matrix

Presenza (1) o assenza (0) del termine, senza contarne le occorrenze  –  è la base del Boolean Retrieval.

| **Termine** | D1 | D2 | D3 | D4 | D5 |
| --- | --- | --- | --- | --- | --- |
| gatto | 1 | 1 | 0 | 0 | 1 |
| cane | 1 | 0 | 1 | 0 | 0 |
| topo | 0 | 0 | 0 | 1 | 1 |

Non permette il ranking: D1 e D2 sono identici per “gatto”, anche se D2 ne parla molto di più.

### Term-Document Count Matrix

Occorrenze grezze ($tf$). Introduce il Bag of Words: distingue D2 da D1, ma la frequenza grezza da sola non implica rilevanza proporzionale.

| **Termine** | D1 | D2 | D3 | D4 | D5 |
| --- | --- | --- | --- | --- | --- |
| gatto | 1 | 3 | 0 | 0 | 1 |
| cane | 1 | 0 | 1 | 0 | 0 |
| topo | 0 | 0 | 0 | 1 | 1 |

### Term-Document Frequency Matrix (Log-Weighted)

Con $w_{t,d} = 1+\log_{10}(tf_{t,d})$ se $tf>0$:
D2(gatto): $tf=3 \to 1+\log_{10}(3)\approx 1.477$. D1(gatto): $tf=1 \to 1$.

| **Termine** | D1 | D2 | D3 | D4 | D5 |
| --- | --- | --- | --- | --- | --- |
| gatto | 1.0 | 1.48 | 0 | 0 | 1.0 |
| cane | 1.0 | 0 | 1.0 | 0 | 0 |
| topo | 0 | 0 | 0 | 1.0 | 1.0 |

“Schiaccia” le frequenze alte per evitare che la ripetizione ossessiva di una parola domini il punteggio (saturazione).

### TF-IDF: calcolo completo

> **Esempio: calcolo IDF**
>
> Con $N=5$: gatto appare in D1, D2, D5 ($df=3$) $\Rightarrow IDF_{gatto} = \log_{10}(5/3) \approx 0.22$. Cane appare in D1, D3 ($df=2$) $\Rightarrow IDF_{cane} = \log_{10}(5/2) \approx 0.40$. Cane è più raro di gatto, quindi pesa quasi il doppio.

> **Esempio: score TF-IDF per la query “gatto cane”**
>
> **D1**: gatto $= 1.0\times0.22=0.22$; cane $=1.0\times0.40=0.40$; **totale $=0.62$**.
>
> **D2**: gatto $=1.48\times0.22\approx0.33$; cane $=0\times0.40=0$; **totale $=0.33$**.
>
> **Conclusione**: D1 vince su D2 (0.62 vs 0.33). Anche se D2 parla molto di “gatti”, D1 menziona entrambi i termini della query, e il termine “cane” (più raro nella collezione) spinge in alto il suo punteggio grazie all'IDF. Il TF-IDF bilancia frequenza locale e rarità globale.

# Indici Inversi e Query Processing

## Struttura: Dictionary e Posting Lists

L'**Indice Inverso** permette ricerche veloci su grandi collezioni evitando la scansione sequenziale del testo. È diviso in due componenti:

> **Definizione: Dictionary**
>
> Contiene l'insieme dei termini unici della collezione. Mantenuto in RAM per accesso rapido, ogni voce punta alla Posting List corrispondente e contiene metadati statistici come la *document frequency* $df_t$ (usata per il calcolo dell'IDF).

> **Definizione: Posting Lists**
>
> Liste (collegate) di *docID* in cui un termine appare, tipicamente ordinate in modo crescente ($docID_1 < docID_2 < \dots$) per ottimizzare intersezione e unione. Possono essere molto voluminose: memorizzate su disco e caricate in blocchi quando necessario (a differenza del dizionario, tipicamente in RAM).

![Dictionary e Posting Lists: ogni termine punta alla propria posting list di coppie (docID, $tf_{t,d}$); $df_t$ e $F_t$ sono statistiche globali usate da BM25.](Files%20and%20Images/fig_dictionary_postinglists.png)

*Dictionary e Posting Lists: ogni termine punta alla propria posting list di coppie (docID, $tf_{t,d}$); $df_t$ e $F_t$ sono statistiche globali usate da BM25.*

## TAAT vs DAAT

Per calcolare i punteggi di una query multi-termine, il sistema attraversa le posting lists secondo due strategie principali.

> **Definizione: Term-at-a-time (TAAT)**
>
> Il sistema elabora completamente la posting list di un termine prima di passare al successivo. Richiede **accumulatori**: un contatore per ogni documento potenzialmente rilevante, aggiornato man mano che si scorre ciascuna lista. Vantaggio: accesso al disco efficiente (letture sequenziali lunghe). Svantaggio: elevato consumo di memoria (accumulatori attivi per tutti i documenti candidati).

![TAAT: gli accumulatori (Direct Access Table/Hash Table) vengono aggiornati scorrendo per intero ciascuna posting list, una alla volta.](Files%20and%20Images/fig_taat.png)

*TAAT: gli accumulatori (Direct Access Table/Hash Table) vengono aggiornati scorrendo per intero ciascuna posting list, una alla volta.*

> **Definizione: Document-at-a-time (DAAT)**
>
> Le posting lists di tutti i termini vengono elaborate in parallelo: puntatori correnti su ciascuna lista avanzano simultaneamente sui docID, calcolando il punteggio finale di un documento (combinando tutti i termini) prima di passare al successivo (il *next docID* più piccolo tra le liste). Efficiente in memoria (nessun grande array di accumulatori) e adatto alle ottimizzazioni top-$k$ (Capitolo *Query Processing Avanzato (Top-*k*)*), ma richiede continui salti (seek) tra le liste.

![DAAT per una query OR: i puntatori avanzano in parallelo sulle liste, unendo i risultati con un merge stile MergeSort.](Files%20and%20Images/fig_daat_or.png)

*DAAT per una query OR: i puntatori avanzano in parallelo sulle liste, unendo i risultati con un merge stile MergeSort.*

> **Esempio: simulazione TAAT per query booleana “Pluto AND NOT Pippo”**
>
> Liste invertite: Pluto: $\{2,3,5,9\}$; Pippo: $\{3,4,5,11,12\}$.
> 1. **Inizializzazione**: insieme di accumulatori vuoto.
> 2. **Processamento di “Pluto”**: si scorre l'intera lista; i docID $\{2,3,5,9\}$ diventano i candidati iniziali negli accumulatori.
> 3. **Processamento di “NOT Pippo”**: si scorre la lista di Pippo $\{3,4,5,11,12\}$; per ogni docID presente, se è negli accumulatori viene rimosso:
>   - doc 3: presente $\to$ rimosso;
>   - doc 4: non presente $\to$ ignorato;
>   - doc 5: presente $\to$ rimosso;
>   - doc 11, 12: non presenti $\to$ ignorati.
>
> 4. **Risultato finale**: accumulatori rimasti $= \{2, 9\}$.
>
> I documenti che soddisfano la query sono **2** e **9**.

## NextGEQ, Binary/Exponential Search, Skip Pointers

L'operazione fondamentale per intersezioni booleane (AND) o in DAAT è trovare il primo docID $\ge$ un target $k$: **NextGEQ($k$)**.

- **Binary Search**: se la lista è in un array contiguo, complessità $O(\log n)$.
- **Exponential Search** (Galloping): quando l'elemento cercato è probabilmente vicino al puntatore corrente, si controllano indici a distanze esponenziali ($1,2,4,8,\dots$) fino a superare il target, poi ricerca binaria solo nell'intervallo identificato.
- **Skip pointers**: collegamenti aggiuntivi nella posting list che permettono di “saltare” gruppi di docID senza scorrerli uno a uno. Se la lista ha lunghezza $L$, lo spaziamento ottimo $\sqrt{L}$ riduce la scansione da $O(L)$ a $O(\sqrt{L})$.

> **Osservazione: perché Binary/Exponential Search da sole non bastano in pratica**
>
> Con $t$ chiamate a NextGEQ su una lista di lunghezza $n$, la ricerca binaria costa complessivamente $\Theta(t\log(n/t))$: teoricamente efficiente, ma poco pratica per due motivi concreti. Primo, è inefficiente quando gli spostamenti richiesti sono molto piccoli (il costo del setup della ricerca binaria domina) o molto grandi (si rifà da capo ogni volta, senza sfruttare la posizione del puntatore precedente). Secondo, e più importante, **richiede accesso casuale** (random access) diretto a un elemento qualsiasi della lista: la maggior parte dei compressori per interi (Elias, VByte, PForDelta) produce un flusso di bit/byte a lunghezza *variabile*, che va necessariamente decodificato in sequenza dall'inizio – non è quindi possibile “saltare” alla posizione $i$-esima senza aver già decodificato tutto ciò che precede. È proprio questa limitazione a rendere necessari gli **Skip pointers** (che precalcolano posizioni di salto note a priori, compatibili con la decodifica sequenziale) e a motivare, nel capitolo sulla compressione, l'importanza di strutture come **Elias-Fano** che offrono invece accesso casuale nativo pur restando compresse.

![Skip pointers su una posting list: permettono di saltare direttamente a un docID lontano senza scorrere gli elementi intermedi.](Files%20and%20Images/fig_skip_pointers.png)

*Skip pointers su una posting list: permettono di saltare direttamente a un docID lontano senza scorrere gli elementi intermedi.*

## Indici Posizionali e Phrase Queries

L'indice inverso standard sa *se* un termine è presente, non *dove*: impossibile rispondere a **Phrase Queries** (es. “Information Retrieval”) dove ordine e adiacenza contano.

> **Definizione: Indice Posizionale**
>
> Ogni voce nella posting list contiene, oltre al docID, la lista delle posizioni (offset) in cui il termine appare:
>
> $$
> \langle term, df_t\rangle \to \dots \to \langle docID, [pos_1, pos_2, \dots]\rangle \to \dots
> $$
>
> Per la frase “T1 T2”: si intersecano prima i docID che contengono entrambi i termini, poi si verifica la prossimità sulle posizioni ($pos(T2) = pos(T1)+1$). Gli indici posizionali aumentano la dimensione dell'indice di un fattore 2–4 rispetto a un indice non posizionale, ma sono essenziali per frasi e operatore NEAR.

![Ricerca di una frase: si cerca un'occorrenza di “information” in posizione $p$ e una di “retrieval” in posizione $p+1$.](Files%20and%20Images/fig_positional_index.png)

*Ricerca di una frase: si cerca un'occorrenza di “information” in posizione $p$ e una di “retrieval” in posizione $p+1$.*

# Query Processing Avanzato (Top-*k*)

Un motore di ricerca reale non ordina mai l'intera collezione: lavora ad “imbuto”. Un primo filtro booleano (AND/OR) riduce miliardi di documenti a poche migliaia di candidati; su questi si applica un ranking semplice ed economico come BM25 per isolare i migliori $k$ (es. $k=1000$); solo sugli ultimissimi risultati (le prime venti posizioni) interviene il costoso re-ranking del modello di Learning-to-Rank. Questo capitolo si occupa esattamente dello stadio intermedio: come recuperare, in modo efficiente, i $k$ documenti con punteggio più alto per una query (tipicamente disgiuntiva, OR, per non perdere candidati) senza dover valutare per intero tutta la collezione.

## Exact Top-*k* e Min-Heap

Il recupero **Exact Top-$k$** garantisce di restituire i veri $k$ documenti con punteggio più alto secondo la funzione di ranking (es. BM25), senza alcuna approssimazione. La strategia più naturale per tenerne traccia mentre si scorrono le posting list è una struttura dati specifica.

> **Definizione: Min-Heap di dimensione $k$**
>
> Mantiene i $k$ migliori documenti trovati finora. La radice contiene il documento con punteggio più basso tra quelli attuali ($k$-esimo miglior documento), che funge da soglia dinamica $\theta$: è proprio la necessità di accedere istantaneamente al più piccolo tra i migliori risultati correnti a motivare la scelta di un Min-Heap piuttosto che un'altra struttura. Un nuovo candidato viene inserito solo se il suo punteggio supera $\theta$; in tal caso il minimo precedente viene espulso e $\theta$ viene aggiornata (aumentata), rendendo il criterio di selezione progressivamente più stringente. Inserimento ed estrazione costano $O(\log k)$, quindi mantenere il top-$k$ su $n$ punteggi processati costa $\Theta(n\log k)$.

> **Esempio: i $k$ valori più grandi di una sequenza**
>
> Sia $k=3$ e supponiamo che il Min-Heap contenga già i valori $\{2.1, 2.3, 3.1\}$, con soglia $\theta=2.1$ (il minimo, alla radice). Scorrendo la sequenza $2.5, 1.5, 3.0, 0.5,\dots$: il valore $2.5$ supera $\theta=2.1$, quindi si estrae il minimo (2.1) e si inserisce 2.5 – l'heap diventa $\{2.3, 2.5, 3.1\}$ e la nuova soglia sale a $\theta=2.3$. Il valore successivo, $1.5$, non supera la nuova soglia ($2.3 > 1.5$) e viene scartato senza toccare l'heap. Questo semplice meccanismo di confronto-ed-eventuale-sostituzione, ripetuto su $n$ elementi, è esattamente ciò che accade – su scala molto più grande – quando il Min-Heap tiene traccia dei migliori $k$ documenti durante la scansione di una query.

> **Osservazione: la strategia “naive” non basta – bisogna toccare ogni posting**
>
> La strategia più semplice per il Top-$k$ Exact è eseguire per intero la query OR in DAAT su tutte le posting list dei termini, calcolando il punteggio completo di *ogni* documento incontrato e provando a inserirlo nel Min-Heap: se il punteggio supera la soglia $\theta$ corrente il documento entra, altrimenti viene scartato. Il problema è che questa strategia è **molto inefficiente**: il controllo con $\theta$ avviene solo *dopo* aver già calcolato il punteggio esatto del documento sommando il contributo di tutti i termini della query per quel documento, quindi non fa risparmiare alcun calcolo. Il costo resta proporzionale al numero totale di posting nelle liste dei termini di query – l'algoritmo deve necessariamente “toccare” (decomprimere e sommare) ogni singola posting di ogni lista, indipendentemente da quanto la soglia $\theta$ sia già alta.

> **Esempio: query OR a quattro termini, Top-1**
>
> Siano le posting list (docID, score) per i termini rust: $\{15,2.5\},\{16,1.5\},\{25,2.0\},\{45,1.5\}$; best: $\{11,0.3\},\{12,0.1\},\{13,0.1\},\{15,0.2\}$; programming: $\{13,0.5\},\{15,1.0\},\{19,1.0\},\{21,1.0\}$; language: $\{10,0.5\},\{13,0.9\},\{25,0.8\},\{29,1.1\}$. Il documento 8 è già in heap come Top-1 con soglia $\theta=2.1$. Scorrendo i docID in ordine crescente, il doc 10 (solo “language”, $0.5$) non batte $\theta$; i doc 11 e 12 (solo “best”, $0.3$ e $0.1$) nemmeno; il doc 13 compare in tre liste (“best” $0.1$ + “programming” $0.5$ + “language” $0.9$ = $1.5$), ancora sotto $\theta=2.1$ – scartato. Solo al doc 15, presente in tre liste (“rust” $2.5$ + “best” $0.2$ + “programming” $1.0$ = $3.7$), il punteggio supera finalmente $\theta$: il documento 8 viene espulso, il doc 15 diventa il nuovo Top-1 e $\theta$ sale a $3.7$. Si noti che *ogni* documento intermedio (10, 11, 12, 13) è stato comunque decompresso e sommato per intero prima di scoprire che non serviva a nulla: è precisamente questo lo spreco che la potatura dinamica elimina.

![Top-$k$ Exact con Min-Heap in DAAT OR: ogni documento incontrato in una qualsiasi posting list viene comunque decompresso e il suo punteggio calcolato per intero prima del confronto con $\theta$ – l'algoritmo tocca ogni singola posting.](Files%20and%20Images/fig_topk_naive_or.png)

*Top-$k$ Exact con Min-Heap in DAAT OR: ogni documento incontrato in una qualsiasi posting list viene comunque decompresso e il suo punteggio calcolato per intero prima del confronto con $\theta$ – l'algoritmo tocca ogni singola posting.*

Questa osservazione è il punto di partenza per la **potatura dinamica**: se si potesse stimare un limite superiore al punteggio di un documento *prima* di calcolarlo per intero, si potrebbero saltare a priori i documenti che non hanno alcuna possibilità di superare $\theta$, evitando di decomprimere e sommare i contributi di tutti i termini per quei documenti.

## Potatura Dinamica e Upper Bound

La **potatura dinamica** (dynamic pruning) evita di valutare documenti senza possibilità di entrare nel top-$k$, confrontando la soglia $\theta$ del Min-Heap con un **Upper Bound** (UB) del punteggio massimo raggiungibile.

> **Definizione: Upper Bound per termine**
>
> Per ogni termine $t$ si pre-calcola $UB_t$: il massimo contributo di punteggio che quel termine può dare a un qualsiasi documento (il massimo peso nella sua posting list). Data una query $Q=t_1 t_2 t_3 t_4$, il punteggio reale è $s(Q,d)=s(t_1,d)+s(t_2,d)+s(t_3,d)+s(t_4,d)$; sostituendo alcuni termini con il loro UB si ottiene una stima per eccesso sempre valida: $s(Q,d) \le UB(t_1)+UB(t_2)+s(t_3,d)+s(t_4,d)$. Se anche questa stima per eccesso (somma degli UB rimanenti più eventuali punteggi già noti) è $<\theta$, il documento può essere scartato *senza* calcolarne il punteggio esatto – a differenza della strategia naive, qui il risparmio avviene *prima* di leggere tutte le posting coinvolte.

## MaxScore e WAND

MaxScore e WAND condividono lo stesso obiettivo (Top-$k$ esatto, saltando quanti più documenti possibile) e lo stesso ingrediente (gli Upper Bound per termine), ma organizzano la potatura in due modi diversi.

> **Definizione: WAND (Weak AND)**
>
> Mantiene un iteratore per ciascuna posting list della query e, a ogni passo, **ordina i termini per il docID corrente puntato** (non per UB). Scorrendo questa lista ordinata, accumula progressivamente gli UB dei termini incontrati finché la somma non supera $\theta$: il primo termine a cui questo accade è il **pivot**. Se anche sommando *tutti* gli UB fino al pivot incluso non si supera $\theta$, nessun documento nell'intervallo corrente può entrare nel top-$k$ e gli iteratori avanzano con `nextGEQ`, saltando intere porzioni di liste; se invece la soglia viene superata, il documento del pivot va valutato per intero.

> **Esempio: WAND passo-passo, Top-1**
>
> Stessi dati dell'esempio precedente. Gli Upper Bound per termine sono $UB(\text{rust})=2.5$, $UB(\text{language})=1.1$, $UB(\text{programming})=0.6$, $UB(\text{best})=0.3$ (qui la posting list di “programming” arriva solo fino a un massimo locale di 0.6). Con $\theta=2.1$, WAND ordina i termini per next docID: language (10), best (11), programming (13), rust (15).
> 1. Si accumula $UB(\text{language})=1.1$: non supera $\theta$, si continua.
> 2. Si aggiunge $UB(\text{best})=0.3$: totale $1.4$, ancora sotto $\theta$.
> 3. Si aggiunge $UB(\text{programming})=0.6$: totale $2.0$, *ancora* sotto $\theta=2.1$ – nonostante si siano già sommati gli UB di tre termini su quattro, non è ancora garantito che valga la pena valutare nulla.
> 4. Si aggiunge infine $UB(\text{rust})=2.5$: totale $1.1+0.3+0.6+2.5=4.5$, che supera ampiamente $\theta=2.1$. Il termine “rust” diventa il pivot, il suo docID corrente (15) è il candidato da valutare: **occorre calcolare il punteggio esatto del documento 15**.
>
> Il punto cruciale è che, superata la soglia solo al quarto e ultimo termine, WAND ha comunque risparmiato tre confronti su quattro rispetto alla lettura completa: i documenti 10, 11 e 13 non sono mai stati toccati con il loro punteggio esatto, solo tramite i rispettivi UB.

![WAND: i termini sono ordinati per next docID; si accumulano gli UB fino al pivot e si verifica se la somma supera la soglia $\theta$.](Files%20and%20Images/fig_wand.png)

*WAND: i termini sono ordinati per next docID; si accumulano gli UB fino al pivot e si verifica se la somma supera la soglia $\theta$.*

> **Definizione: MaxScore**
>
> Ordina invece i termini della query per $UB_t$ **decrescente** e li divide in due gruppi fissi: le liste **essenziali** (UB alto) e le liste **non essenziali** (UB basso), scelte in modo che la somma degli UB non essenziali resti contenuta. Si processano le liste essenziali in DAAT OR; per ogni documento incontrato in una lista essenziale, si stima il punteggio massimo possibile sommando il punteggio *esatto* già noto nelle liste essenziali con la somma degli UB (non i punteggi esatti) delle liste non essenziali. Solo se questa stima supera $\theta$ si procede a recuperare anche il punteggio esatto nelle liste non essenziali per quel documento specifico.

> **Esempio: MaxScore passo-passo, Top-1**
>
> Con gli UB $UB(\text{rust})=2.5$, $UB(\text{language})=1.1$, $UB(\text{programming})=1.0$, $UB(\text{best})=0.3$ (qui la posting list di “programming” rivela valori fino a 1.0), MaxScore sceglie come **essenziali** i due termini a UB più alto – rust e language – e come **non essenziali** programming e best, la cui somma di UB è $1.0+0.3=1.3$. Con $\theta=2.1$ si scorrono le liste essenziali in ordine di docID:
> 1. **Doc 10** (in “language”, score esatto 0.5): stima $= UB(\text{programming})+UB(\text{best})+s(\text{language},10) = 1.0+0.3+0.5=1.8 < 2.1$. Scartato senza mai leggere le liste non essenziali per questo documento.
> 2. **Doc 13** (in “language”, score esatto 0.9): stima $=1.0+0.3+0.9=2.2 > 2.1$. Questa volta la stima *supera* la soglia: il documento *potrebbe* entrare in classifica, quindi va verificato per intero recuperando anche i punteggi reali nelle liste non essenziali. Il punteggio esatto risulta $s(\text{language},13)+s(\text{programming},13)+s(\text{best},13) = 0.9+0.5+0.1=1.5$, che **non** supera $\theta=2.1$: il doc 13 viene comunque scartato. È la dimostrazione che superare il controllo sull'Upper Bound è una condizione *necessaria ma non sufficiente* per entrare nel top-$k$ – garantisce solo che valga la pena verificare, non che il documento vinca davvero.
> 3. **Doc 15** (in “rust”, score esatto 2.5): stima $=1.0+0.3+2.5=3.8>2.1$. Anche qui serve la verifica completa: sommando i punteggi reali nelle liste non essenziali per il doc 15 si supera $\theta$, quindi il documento 15 diventa il nuovo Top-1.
>

![MaxScore: i termini sono divisi in “essenziali” (alto UB) e “non essenziali” (basso UB); il documento va verificato per intero solo se punteggio parziale più UB residuo superano $\theta$, ma può comunque essere scartato dopo la verifica.](Files%20and%20Images/fig_maxscore.png)

*MaxScore: i termini sono divisi in “essenziali” (alto UB) e “non essenziali” (basso UB); il documento va verificato per intero solo se punteggio parziale più UB residuo superano $\theta$, ma può comunque essere scartato dopo la verifica.*

> **Osservazione: perché entrambi funzionano, e quanto guadagnano in pratica**
>
> WAND e MaxScore attaccano lo stesso problema da due angolazioni complementari: WAND decide *quale documento* valutare (muovendosi per docID crescente e usando gli UB solo per decidere se accettare il pivot), mentre MaxScore decide *quali termini* vale la pena leggere per intero per un dato documento (separando stabilmente essenziali da non essenziali). In esperimenti su collezioni reali (es. Gov2, TREC), per query Top-10 con più termini un DAAT OR “ranked” senza alcuna potatura può arrivare a centinaia di millisecondi per query (l'ordine delle centinaia cresce linearmente col numero di termini), mentre MaxScore e WAND restano tipicamente nell'ordine di pochi millisecondi – un guadagno di uno o due ordini di grandezza, a fronte di un overhead di spazio minimo per memorizzare gli UB.

# Compressione dei Dati

Comprimere un indice riduce lo spazio su disco/RAM e la banda di I/O necessaria a caricarlo, ma introduce un costo di decodifica: la **compressione ratio** $CR = |B|/|C(B)|$ (dimensione originale su dimensione compressa) misura quanto si guadagna in spazio, mentre la velocità di decodifica misura quanto si perde in tempo CPU. Questo **trade-off spazio/tempo** è centrale nei motori di ricerca moderni: non è più sufficiente decomprimere per intero un blocco e poi calcolarci sopra (*decompress-and-compute*), perché il costo di decompressione stesso diventerebbe il collo di bottiglia dominante. L'obiettivo delle tecniche di questo capitolo è quindi permettere, per quanto possibile, un **calcolo diretto sui dati compressi** (o una decompressione parziale e mirata, come nello skipping), evitando di pagare il costo pieno di decompressione per le porzioni di indice che la potatura dinamica (Capitolo *Query Processing Avanzato (Top-*k*)*) ha già deciso di scartare.

## Sharding e distribuzione

I sistemi IR moderni gestiscono volumi che superano la capacità di una singola macchina: l'indice inverso viene partizionato e distribuito su più nodi (**Sharding**).

> **Osservazione: perché serve lo sharding**
>
> Tre necessità distinte guidano l'adozione dello sharding: la **scalabilità** (una singola macchina non può contenere né servire una collezione che cresce senza limite, quindi si aggiungono nodi orizzontalmente), le **performance** (distribuire il carico su più nodi riduce il lavoro di ciascuno e abbassa la latenza per query), e la **fault tolerance** (con repliche dei dati su nodi diversi, il guasto di un singolo server non rende l'indice irraggiungibile). Ne segue una distinzione pratica: se cresce la *dimensione* della collezione si aggiungono **shard** (nuovi frammenti di dati); se cresce il *traffico* di query si aggiungono **repliche** (copie ridondanti degli shard esistenti, per servire più query in parallelo).

L'architettura tipica prevede un insieme di **Index Server** organizzati in shard $s_1,\dots,s_k$; ogni shard è replicato su più copie identiche, e un nodo **broker** riceve la query, la instrada alle repliche opportune e ne raccoglie i risultati.

## Proprietà dei codici

I codici usati per comprimere docID e frequenze devono avere **decodificabilità univoca**: ogni sequenza di bit codificata corrisponde a un solo messaggio originale.

> **Osservazione: perché serve un codice prefix-free**
>
> Si preferiscono codici **prefix-free** (istantanei): nessuna parola di codice è prefisso di un'altra. Questo permette al decoder di riconoscere la fine di un codice e l'inizio del successivo senza dover leggere bit futuri. Se un codice fosse prefisso di un altro, il decodificatore non saprebbe se fermarsi o continuare a leggere per identificare un numero più lungo  –  impossibile separare correttamente i valori in un flusso continuo di bit come una posting list compressa.

![Un codice non prefix-free (qui $B(x)$) genera ambiguità in decodifica: la stessa sequenza di bit può corrispondere a più sequenze di numeri diverse.](Files%20and%20Images/fig_dgap_ambiguous.png)

*Un codice non prefix-free (qui $B(x)$) genera ambiguità in decodifica: la stessa sequenza di bit può corrispondere a più sequenze di numeri diverse.*

### Codice Unario e Codici Elias

Il **Codice Unario** rappresenta $x$ come $x-1$ bit `1' seguiti da un terminatore `0' (es. $3 \to 110$). Efficiente solo per numeri molto piccoli.

> **Definizione: Elias Gamma ($\gamma$)**
>
> Codifica $x$ con: (a) la sua lunghezza binaria (meno un bit) in unario, seguita da (b) il valore binario di $x$ troncato del bit più significativo. Lunghezza totale: $2\lfloor\log_2 x\rfloor+1$.

![Costruzione di Elias Gamma: $\gamma(5) = U(3)\cdot\text{bin}(5)$ senza il bit più significativo.](Files%20and%20Images/fig_elias_gamma.png)

*Costruzione di Elias Gamma: $\gamma(5) = U(3)\cdot\text{bin}(5)$ senza il bit più significativo.*

> **Definizione: Elias Delta ($\delta$)**
>
> Migliora la compressione per numeri grandi codificando la componente lunghezza stessa con Elias Gamma invece che in unario.

![Costruzione di Elias Delta: la lunghezza $|bin(x)|$ è codificata a sua volta con Elias Gamma invece che in unario, migliorando la compressione per numeri grandi.](Files%20and%20Images/fig_elias_delta.png)

*Costruzione di Elias Delta: la lunghezza $|bin(x)|$ è codificata a sua volta con Elias Gamma invece che in unario, migliorando la compressione per numeri grandi.*

> **Esempio: codifica di $x=11$ in Elias Gamma e Delta**
>
> **Elias Gamma**:
> 1. $11 = 1011_2$ (4 bit).
> 2. Numero di bit meno uno: $N=3$.
> 3. Prefisso unario: $N$ zeri seguiti da un uno $\to$ `0001`.
> 4. Suffisso: bit di $x$ esclusi il bit più significativo $\to$ `011`.
>
> Risultato: $\gamma(11) = \texttt{0001\,011}$ (7 bit).
>
> **Elias Delta**:
> 1. $L = 1+\lfloor\log_2 11\rfloor = 4$.
> 2. Si codifica $L=4$ in Elias Gamma: $4=100_2$, bit meno uno $=2\to$ unario `001`, suffisso `00` $\Rightarrow \gamma(4)=\texttt{00100}$.
> 3. Si appendono i bit di $11$ esclusi il primo: `011`.
>
> Risultato: $\delta(11) = \texttt{00100\,011}$ (8 bit).

> **Esempio: codifica di $x=14$ in Elias Gamma e Delta**
>
> **Elias Gamma**: $14 = 1110_2$ (4 bit) $\Rightarrow N=3 \to$ prefisso `0001`; suffisso (senza bit più significativo) `110`. Risultato: $\gamma(14) = \texttt{0001\,110}$ (7 bit).
>
> **Elias Delta**: $L = 1+\lfloor\log_2 14\rfloor = 4 \Rightarrow \gamma(4) = \texttt{00100}$ (come sopra); suffisso di 14 senza il primo bit: `110`. Risultato: $\delta(14) = \texttt{00100\,110}$ (8 bit).

### Variable-Byte (VByte)

A differenza dei codici Elias (livello di bit), il **Variable-Byte** lavora allineato al byte: ogni byte usa 7 bit di payload e 1 bit di controllo (1 = byte successivo appartiene allo stesso numero, 0 = ultimo byte del numero). Decodifica molto più veloce sulle CPU moderne.

![Variable-Byte: ogni numero occupa un numero variabile di byte, ciascuno con 7 bit di payload e 1 bit di continuazione.](Files%20and%20Images/fig_vbyte.png)

*Variable-Byte: ogni numero occupa un numero variabile di byte, ciascuno con 7 bit di payload e 1 bit di continuazione.*

> **Osservazione: VByte vs codici Elias**
>
> Il VByte è più veloce da decodificare, sfruttando operazioni native della CPU allineate al byte ed evitando manipolazioni bit-a-bit costose in cicli di clock. Di contro, spreca più spazio per numeri piccoli: usa sempre almeno 8 bit (di cui uno di controllo), mentre Elias Gamma può rappresentare piccoli interi con pochissimi bit. Per numeri grandi, Elias Delta comprime meglio del VByte. La scelta è quindi un trade-off velocità/densità.

## List Encoders: d-gaps e Combinatorial Lower Bound

Poiché nelle posting list i docID sono ordinati crescenti, invece di memorizzare valori grezzi (sempre più bit al crescere dei docID) si memorizzano i **d-gaps**: $d_i - d_{i-1}$, generalmente numeri piccoli, codificabili efficientemente (VByte, Elias).

> **Definizione: Combinatorial Lower Bound**
>
> Scegliendo $n$ elementi da un universo di dimensione $U$, esistono $\binom{U}{n}$ liste distinte possibili: nessuno schema a lunghezza fissa può quindi rappresentarle tutte con meno di $\lceil \log_2 \binom{U}{n}\rceil$ bit in media (principio della piccionaia). Per l'approssimazione di Stirling, questo valore è circa $n\log_2(U/n) + 1.44n$ bit.

> **Osservazione: perché in pratica si va sotto il lower bound medio**
>
> Il Combinatorial Lower Bound è una media calcolata assumendo che tutte le $\binom{U}{n}$ liste possibili siano ugualmente probabili – ma le posting list reali non lo sono affatto: sono fortemente **clustered** (raggruppate), non distribuite uniformemente nell'universo. Su collezioni reali (Gov2, ClueWeb09) circa il 65% dei d-gaps vale esattamente 1, con frequenza che decresce rapidamente al crescere del gap. Un compressore che sfrutta questa regolarità può quindi comprimere le liste *tipiche* con molti meno bit di quanto il lower bound medio farebbe pensare: il **Binary Interpolative Coding** (sotto) è il caso estremo di questo principio, capace di codificare un'intera sequenza di interi consecutivi con *zero* bit aggiuntivi.

## Packing e Interpolazione

- **Simple9 / Simple16**: impacchettano il maggior numero possibile di d-gaps in una word di 32 bit, usando alcuni bit selettore per indicare lo schema di partizionamento (es. 28 numeri da 1 bit, o 14 da 2 bit). Riduce drasticamente il branching della CPU.
- **PFor (Patched Frame of Reference)**: robusto agli outlier. Un singolo valore anomalo in un blocco altrimenti di numeri piccoli forzerebbe una larghezza di bit inutilmente grande per *tutti* gli elementi del blocco (es. il blocco $[1,1,1,\dots,1,8247,1,\dots,1]$ richiederebbe $\lceil\log_2 8247\rceil=14$ bit per ogni numero, mentre quasi tutti starebbero in 1 bit). PFor sceglie una base $b$ e un parametro $k$ tali che la maggior parte degli interi del blocco (es. il 90%) cada nell'intervallo $[b, b+2^k-1)$: ciascuno di questi viene codificato come il semplice delta $x-b$ in $k$ bit (da cui il nome **PForDelta**). I valori che eccedono l'intervallo ricevono una codeword speciale di eccezione (pari a $2^k-1$) e vengono spostati in una lista separata dedicata alle eccezioni. La decompressione resta rapidissima per la stragrande maggioranza dei dati, pagando l'overhead della lista di eccezioni solo per i pochi outlier.
- **Binary Interpolative Coding**: ideale per liste dense e clustered (stopword, termini comuni). Dato un intervallo $[l,h]$ con $n$ elementi noti (inizialmente $l\le L[1]$, $h\ge L[n]$), si codifica l'elemento mediano $L[m]$ con $m=\lceil n/2\rceil$: sapendo che $l\le L[m]\le h$, basta scrivere lo scarto $L[m]-l$ in $\lceil\log_2(h-l)\rceil$ bit. Il procedimento si ripete ricorsivamente sulle due metà, aggiornando gli estremi a $(l, L[m]-1)$ e $(L[m]+1, h)$. La proprietà cruciale: se a un certo punto della ricorsione l'intervallo $h-l$ coincide esattamente con la lunghezza $r$ del sotto-intervallo (cioè si è isolata una corsa di $r$ interi consecutivi), la codifica di quel nodo costa **zero bit** – il valore è già determinato senza ambiguità dagli estremi.

![Binary Interpolative Coding su $L=[3,4,7,13,14,15,21,25,36,38,54,62]$ ($l=0$, $h=62$): a ogni nodo si codifica il mediano e si ricorre sulle due metà con estremi aggiornati. Il nodo foglia $[14]$ ha $l=h=14$: l'intervallo è già di ampiezza zero, quindi “No coding needed!” – esempio concreto di come la regolarità della sequenza permetta di scendere sotto il costo per elemento del caso generico.](Files%20and%20Images/fig_bic_tree.png)

*Binary Interpolative Coding su $L=[3,4,7,13,14,15,21,25,36,38,54,62]$ ($l=0$, $h=62$): a ogni nodo si codifica il mediano e si ricorre sulle due metà con estremi aggiornati. Il nodo foglia $[14]$ ha $l=h=14$: l'intervallo è già di ampiezza zero, quindi “No coding needed!” – esempio concreto di come la regolarità della sequenza permetta di scendere sotto il costo per elemento del caso generico.*

## Elias-Fano

> **Definizione: Elias-Fano**
>
> Si avvicina al Combinatorial Lower Bound supportando l'**accesso casuale** (random access), impossibile con i d-gaps standard. Divide ogni numero ordinato $x$ in bit bassi (*low bits*, memorizzati esplicitamente in un array $L$) e bit alti (*high bits*, rappresentati come array di bit $H$ costruito concatenando rappresentazioni unarie delle quantità di numeri che condividono lo stesso prefisso). Lo spazio totale è $n\log(U/n)+2n$ bit; l'accesso diretto $Access(i)$ costa $O(1)$, mentre $NextGEQ(x)$ costa $O(\log(U/n))$ sfruttando Rank e Select sull'array $H$.

Il **Partitioned Elias-Fano** divide la lista in blocchi e applica Elias-Fano su ciascuno indipendentemente, ottimizzando il numero di bit per i low bits in base alle caratteristiche locali dei dati, migliorando le prestazioni su distribuzioni irregolari.

## Query Rank e Select

Per navigare strutture succinte (es. il vettore $H$ di Elias-Fano) servono due operazioni in tempo costante $O(1)$:
- **Rank**$(B,i)$: numero di bit `1' nel vettore $B$ fino alla posizione $i$ inclusa.
- **Select**$(B,i)$: posizione dell'$i$-esimo bit `1' nel vettore $B$.

Implementate con strutture ausiliarie che memorizzano conteggi pre-calcolati per blocchi e super-blocchi, evitando il conteggio lineare.

# Learning to Rank I – Fondamenti

## Architettura e flusso dei dati

Il Learning to Rank si inserisce nell'architettura a due fasi già vista nel Capitolo 1. Nella fase **offline**, la Document Collection viene indicizzata (Inverted Index) e, in parallelo, un Feature Processor estrae le caratteristiche dei documenti in un Document Features Repository; sempre offline, un insieme di Training Data alimenta un processo di **Training** che genera il *Learning-to-rank Model*. Nella fase **online**, la query dell'utente viene espansa, il Query Processing interroga l'indice per individuare i documenti candidati, si esegue il Feature Lookup and Computation attingendo al repository, e infine la *Learned Ranking Function* (guidata dal modello addestrato) calcola il punteggio finale e produce la pagina dei risultati.

## Funzioni di ranking generali

Per capire come opera un modello di ranking, conviene decomporre una **funzione di ranking generale** nelle sue componenti astratte.

> **Definizione: Decomposizione di una funzione di ranking**
>
> Data una query $q$ e un documento $d$, si distinguono: una **query representation** $\phi(q)$, una **document representation** $\psi(d)$, e una **query-document representation** $\eta(q,d)$ che cattura l'interazione diretta tra i due. Queste tre rappresentazioni alimentano insieme una **aggregation function** $f$, che calcola il punteggio di rilevanza finale:
>
> $$
> s(q,d) = f\big(\phi(q),\,\psi(d),\,\eta(q,d)\big)
> $$
>

### BOW Ranking Functions

Nei modelli classici basati su Bag-of-Words questo schema si semplifica drasticamente. Query e documenti sono rappresentati unicamente come **vettori sparsi BOW** (multi-insiemi di parole): non ci sono *query-document features*, cioè la componente $\eta(q,d)$ viene completamente scartata dal calcolo. La funzione di aggregazione $f$ è, in questi casi, una formula analitica fissa come la **cosine similarity** o **BM25**. Queste rappresentazioni sparse sono archiviate negli inverted index, che costituiscono la spina dorsale (*backbone*) dei motori di ricerca Web commerciali.

### Estensione del framework: nuovi segnali

Finora query e documenti sono stati trattati come semplici multi-insiemi di parole. Ma esistono molti altri **segnali** sfruttabili nel framework generale, che vanno oltre il confronto lessicale. Lato documento: la struttura in campi o zone, l'arricchimento con testo esterno come gli *anchor* (testo dei link entranti), e segnali strutturali/comportamentali come *In-Links*, *Out-Links*, *PageRank*, numero di *click*, social links. Lato query: numero di termini, popolarità nei log del motore, informazioni dal profilo utente. Reintroducendo tutti questi dati, il framework torna a sfruttare pienamente la componente $\eta(q,d)$ nel calcolo della funzione di aggregazione.

## Machine Learning: possiamo imparare $f$?

L'inclusione di tutti questi segnali porta a una domanda: **possiamo imparare $f$**? Impararla significa affidarsi al **Machine Learning**, disciplina che usa tecniche statistiche per permettere ai sistemi di apprendere – cioè di migliorare progressivamente le prestazioni su un compito specifico attingendo dai dati, senza essere programmati esplicitamente. Questo approccio richiede due precondizioni: l'**esistenza di dati** e la definizione di un **problema di ottimizzazione** (un task e una misura da ottimizzare).

> **Definizione: Task di Machine Learning**
>
> A seconda che al sistema venga fornito o meno un “segnale” di apprendimento (feedback), i task si dividono in due categorie:
> - **Supervised learning**: al sistema vengono presentati input di esempio con i rispettivi output desiderati, forniti da un “insegnante”; l'obiettivo è imparare una regola generale che mappi input a output. Nel nostro contesto: problemi di *Classificazione* o *Regressione*.
> - **Unsupervised learning**: non vengono fornite etichette; il sistema deve trovare autonomamente una struttura nei dati di input.
>

### Terminologia utile

Ogni elemento del dataset è detto **osservazione** o **istanza**. Ogni istanza è descritta da un insieme di **feature**, cioè proprietà misurabili, ed è accompagnata (nel supervised learning) da una **label** che rappresenta il valore da prevedere. Sfruttando lo spazio delle feature e la supervisione delle label, si distinguono due task: la **Regressione**, che predice una quantità numerica reale, e la **Classificazione**, che assegna etichette discrete a istanze la cui categoria non è nota in anticipo.

Ogni attività di apprendimento si articola in due fasi: la **training phase** (tipicamente offline), in cui il modello viene costruito adattandolo (*fit*) a un dato insieme di dati, e la **testing phase** (tipicamente online), in cui il modello già addestrato viene usato per predire la classe/label di nuovi dati.

## Il framework generale di apprendimento

Il ciclo di apprendimento si comprende con un parallelo con i puzzle: per ogni gioco abbiamo il problema e la sua soluzione; il nostro cervello produce un tentativo, lo confronta con la soluzione corretta, e dalla differenza impara – ripetendo per molti giochi si migliora. Formalizzando: un'istanza di addestramento viene presentata all'algoritmo, che produce una predizione (*actual output*); un “teacher” fornisce l'output desiderato; la differenza tra i due è la **loss** (o error). La loss viene restituita all'algoritmo e usata per aggiornare i parametri interni del modello. Il processo si ripete su tutte le istanze del training set (un giro completo è un **epoch**) e l'intero procedimento viene reiterato per più **learning iterations / training epochs**.

## Il task del Learning to Rank (LtR)

Applicando queste dinamiche al retrieval otteniamo il **Learning to Rank (LtR)**: l'obiettivo è produrre e ottimizzare le liste ordinate di risultati. La fase di addestramento richiede un ampio training set di query, ciascuna affiancata dall'ordinamento ideale dei documenti rispetto a essa.

> **Osservazione: si impara il ranking, non l'etichetta**
>
> Anche se i modelli LtR addestrati sono tecnicamente dei regressori che assegnano un punteggio a ogni singolo candidato, lo scopo ultimo e misurabile del sistema è *indovinare l'ordinamento complessivo* della lista, non replicare precisamente le specifiche etichette numeriche.

Operativamente, invece di processare singole parole, il sistema gestisce l'interazione tra query $q$ e documento $d$ rappresentandola come un **feature vector**, denotato $\langle q,d\rangle = \langle f_0, f_1, \dots\rangle$, che funge da input per il modello. Il modello agisce come una *black box* che riceve query e documenti e restituisce una lista ordinata. Le etichette $y$ indicano il grado di pertinenza reale: un esempio tipico usa una scala a cinque valori, da 0 (irrilevante) a 4 (perfettamente rilevante).

### Feature in LtR

Le innumerevoli feature usate nei sistemi moderni (Google ne dichiarava oltre 200 già nel 2008) si categorizzano in tre macrogruppi:
- **Query-only features**: assumono lo stesso valore per ogni documento della sessione, essendo legate solo alla query (es. tipologia o lunghezza della query).
- **Query-independent features**: caratteristiche intrinseche del documento, costanti a prescindere dalla ricerca (es. PageRank, lunghezza dell'URL).
- **Query-dependent features**: generate in tempo reale dall'interazione tra query e documento (es. punteggio BM25, frequenza della query in grassetto, click passati per quella specifica coppia query-URL).

### La pipeline del Learning to Rank

L'integrazione di un modello LtR in un motore di ricerca avviene attraverso una **pipeline** in due fasi. Nel **First step**, la query viene processata da un **Base Ranker** che interroga il Document Index ed estrae rapidamente un sottoinsieme iniziale di **$N$ documenti** candidati. Nel **Second step**, questo primo bacino passa a un **Top Ranker**, che sfrutta il Learning-to-Rank Algorithm e le Features per riordinare e selezionare un insieme più ristretto di **$K$ documenti**, i quali compongono la pagina dei risultati.

![Pipeline LtR: il Base Ranker (First step) estrae $N$ candidati dal Document Index, il Top Ranker (Second step), guidato dal Learning to Rank Algorithm e dalle Features, li riordina selezionando i $K$ risultati finali.](Files%20and%20Images/fig_ltr_pipeline.png)

*Pipeline LtR: il Base Ranker (First step) estrae $N$ candidati dal Document Index, il Top Ranker (Second step), guidato dal Learning to Rank Algorithm e dalle Features, li riordina selezionando i $K$ risultati finali.*

> **Osservazione: vincoli di budget**
>
> Le considerazioni sul *budget* di tempo e risorse sono critiche per i motori commerciali: il costo computazionale del modello LtR, sensibilmente più alto di funzioni semplici, deve rientrare nel tempo disponibile per processare il flusso di query in ingresso, impattando direttamente il **throughput** del sistema. È questo vincolo a motivare l'architettura a due stadi e le ottimizzazioni di efficienza trattate nei capitoli successivi.

## Un esempio: classificazione per l'ad hoc IR

Per concretizzare, consideriamo l'uso di una tecnica di classificazione per il recupero *ad hoc*. Si raccoglie un corpus di triple $(q, d, r)$, dove $r$ è la rilevanza (qui binaria per semplicità, ma può essere multi-classe). Ogni coppia query-documento è rappresentata da un vettore $x=(\alpha,\omega)$, dove $\alpha$ è la **cosine similarity** e $\omega$ è la **minimum query window size**, l'ampiezza minima della porzione di testo del documento che include tutti i termini della query, indipendentemente dal loro ordine – una misura di *term proximity*.

| **Es.** | docID | query | $\alpha$ (cosine) | $\omega$ (prox.) | giudizio |
| --- | --- | --- | --- | --- | --- |
| $\Phi_1$ | 37 | linux operating system | 0.032 | 3 | relevant |
| $\Phi_2$ | 37 | penguin logo | 0.020 | 4 | nonrelevant |
| $\Phi_3$ | 238 | operating system | 0.043 | 2 | relevant |
| $\Phi_4$ | 238 | runtime environment | 0.004 | 2 | nonrelevant |
| $\Phi_5$ | 1741 | kernel layer | 0.022 | 3 | relevant |
| $\Phi_6$ | 2094 | device driver | 0.030 | 2 | relevant |
| $\Phi_7$ | 3191 | device driver | 0.027 | 5 | nonrelevant |

Si definisce una **funzione di punteggio lineare** $Score(d,q)=a\alpha + b\omega$. Il classificatore lineare deve determinare i pesi $a$, $b$ e una soglia $\Theta$ tali da decidere *relevant* se $Score(d,q)>\Theta$, *irrelevant* altrimenti – esattamente come nella text classification, dove la retta $a\alpha+b\omega=\Theta$ è la *decision surface* che separa le due classi.

## Le tre famiglie di approcci al Learning to Rank

Gli algoritmi di LtR si dividono in tre famiglie, distinte dal modo in cui è calcolata la funzione di costo (loss).

> **Definizione: Pointwise**
>
> Come nell'esempio lineare precedente, ogni coppia query-documento è valutata a sé stante e le viene assegnato un punteggio. L'obiettivo è predire tale punteggio, configurando il task come *regressione* o *classificazione multiclasse*. Il grande limite è che ignora totalmente la posizione finale del documento nella lista dei risultati.

> **Definizione: Pairwise**
>
> L'unità di misura non è il singolo documento ma la **coppia**. Al sistema vengono fornite preferenze a coppie (es. per la query $q$, $d_1$ è migliore di $d_2$) e l'obiettivo (spesso una classificazione binaria) è assegnare punteggi che preservino tali preferenze. *RankNet* è l'algoritmo pairwise di riferimento, e usa una funzione di perdita **derivabile**. Anche il pairwise, però, non valuta la rilevanza del documento nella sua specifica posizione finale.

> **Definizione: Listwise**
>
> Al sistema viene fornito il ranking ideale dell'intera lista per ogni query. L'obiettivo è massimizzare la qualità globale dell'elenco valutandolo nella sua interezza, ottimizzando direttamente metriche come **NDCG@K**. Barriera principale: solitamente *non* è possibile applicarvi direttamente le tecniche di ottimizzazione basate sulle derivate, come la (Stochastic) Gradient Descent.

## BM25 e il modello BM25F

Passando dai concetti strutturali ai modelli matematici, **Okapi BM25** (già introdotto nel Cap. 5) è una funzione di ranking probabilistica, stato dell'arte (SOTA) tra i metodi BOW e superiore alla cosine similarity. Sfruttando l'ipotesi di indipendenza dei termini, approssima la probabilità che un documento sia rilevante, pesando frequenza dei termini e lunghezza del documento:

$$
BM25(d,q) = \sum_{t} IDF_t \cdot \tau(F_t), \qquad F_t = \frac{f_{t,d}}{1 - b + b\cdot l_d/L}, \qquad \tau(F_t) = \frac{F_t}{k + F_t}
$$

dove $IDF_t=\log(N/n_t)$, $l_d$ è la lunghezza del documento, $L$ quella media della collezione; $\tau$ è la **funzione di saturazione** che introduce la non-linearità nel contributo della frequenza, mentre $b$ controlla la **normalizzazione di lunghezza** (*pivoted length normalization*). Senza tuning, gli iperparametri di default sono $k\in[1.2,2]$ e $b=0.75$.

> **Definizione: BM25F**
>
> I documenti reali (paper, pagine web) non sono blocchi monolitici ma insiemi di **campi** strutturati (titolo, abstract, autori; oppure titolo, url, corpo, ancore). Il **BM25F** estende BM25 calcolando la frequenza a livello di singolo campo:
>
> $$
> BM25F(d,q) = \sum_{t} IDF_t\cdot\tau(F_t), \qquad F_t = \sum_{s} \frac{w_s \cdot f_{t,s}}{1 - b_s + b_s\cdot l_s/L_s}
> $$
>
> dove $f_{t,s}$ è la frequenza del termine nel campo $s$, $l_s$ e $L_s$ la lunghezza del campo e la sua media, e soprattutto $w_s$ è un **peso** che definisce l'importanza relativa del campo $s$. Questo genera un'esplosione dei parametri: mentre BM25 ha solo 2 parametri liberi ($b,k$), BM25F ne ha **$2S+1$** (i pesi $w_s$, le penalizzazioni $b_s$ per ogni campo, più la saturazione globale $k$), rendendo impossibile la calibrazione manuale.

## Learning to Rank applicato a BM25F

L'elevato numero di parametri di BM25F costringe ad affidarsi al Learning to Rank per trovare la configurazione ottimale. Il setup: un dataset dove ogni query ha candidati annotati con rilevanza da 0 a 4; lo spazio delle ipotesi è l'insieme di tutte le possibili funzioni BM25F; l'obiettivo è massimizzare una metrica listwise, tipicamente **NDCG@10**, con gain esponenziale $2^{rel}-1$ (0 per rilevanza 0, fino a $2^4-1=15$ per rilevanza massima).

> **Osservazione: perché non si può applicare direttamente la Gradient Descent**
>
> Vogliamo apprendere un modello $h$ (la funzione BM25F con parametri $\theta$) per ordinare i documenti: *Risultati* $=$ `sort`$\{h(d_1),h(d_2),\dots\}$. Il problema è che le metriche come NDCG dipendono dalle *posizioni finali* dei documenti, non dai punteggi numerici prodotti dalla funzione di scoring. Servirebbe quindi calcolare il gradiente dell'operazione di `sort` rispetto a $\theta$ – ma `sort` **non è una funzione continua né derivabile**. Di conseguenza, non si può applicare direttamente la discesa del gradiente.

Per aggirare l'ostacolo, l'IR adotta due vie:
- Si minimizza una **proxy loss pairwise** (ad esempio il costo di RankNet, che è differenziabile), pur sapendo che ottimizzare le coppie non equivale alla perfezione listwise.
- Si ricorre a soluzioni più efficaci come **Lambda-MART**, che usa una funzione di costo capace di approssimare i gradienti di NDCG. Il MART, noto anche come **Gradient Boosted Regression Tree (GBRT)**, è un ensemble di alberi di regressione (libreria di riferimento: LightGBM). I dettagli di RankNet e LambdaMART sono trattati nel Capitolo 10.

> **Osservazione: il difetto dell'approccio Pairwise puro**
>
> L'approccio Pairwise si limita a minimizzare il numero totale di coppie classificate scorrettamente. Ma per ottimizzare NDCG, le coppie che coinvolgono i primi risultati (*top result pairs*) sono molto più importanti di quelle nelle posizioni basse. Per questo il semplice conteggio delle violazioni di coppia, senza pesarle in base alla posizione, non è un buon indicatore per l'NDCG – ed è esattamente il problema che LambdaMART risolve pesando ogni coppia con $|\Delta NDCG|$.

# Learning to Rank II

## Machine Learning per IR sulle Hand-Crafted Features

Nel dettaglio operativo, dati una query $q$ (con $n$ token) e un documento $d$ (con $m$ token), un modulo **Feature Extractor** calcola per la coppia $(q,d)$ un **Hand-Crafted Feature Vector**: un vettore di feature numeriche *progettate a mano* da esperti (score BM25, lunghezza del documento, PageRank, presenza dei termini di query nel titolo, ecc.). Questo vettore entra in un modello di Machine Learning – lo stato dell'arte è una **foresta di migliaia di alberi di regressione** – che produce un unico numero, il relevance score $s_{q,d}$ usato per ordinare i documenti.

Il compito è complesso perché consiste nell'*imparare il ranking, non la label*: le misure di qualità del ranking (NDCG, MAP) non sono facilmente derivabili e la discesa del gradiente non è applicabile direttamente. La soluzione è definire una **proxy loss function**: una funzione differenziabile con andamento il più possibile fedele al costo originale, che si possa ottimizzare al posto della metrica non derivabile.

## Algoritmi Point-Wise

Gli algoritmi **Point-Wise** vedono il problema documento per documento, non come confronto tra documenti. Ogni istanza di training è una coppia $(d_i, y_i)$, dove $d_i$ è il vettore di feature per la coppia query-documento e $y_i$ il giudizio di rilevanza ($0,1,2,\dots$). Poiché ogni istanza è trattata separatamente, senza usare informazioni sugli altri documenti della stessa query, si può scegliere una loss standard di ML su $y_i$: *Regression* (il modello predice un valore reale $\hat y_i$), *Multi-Class Classification* (i livelli di rilevanza discreti $0,1,2,3$ trattati come classi) o *Ordinal Regression* (come la classificazione ma tenendo conto dell'ordine $0<1<2<3$).

> **Esempio: modello di regressione con GBRT**
>
> Scegliendo la regressione, il modello tipico è il **GBRT** (Gradient Boosting Regression Trees, una foresta di alberi di regressione “boosted”), che minimizza la **SSE** (Sum of Squared Errors): $SSE = \sum_i (y_i-\hat y_i)^2$.

Il grande limite del Point-Wise è che ignora la posizione finale del documento nella lista: un errore su un documento in cima pesa quanto lo stesso errore su uno in fondo.

## Gradient Boosting Regression Trees

### Decision Trees e Regression Trees

Un **decision tree** è un albero in cui i nodi interni rappresentano test sulle feature, i rami sinistro/destro i risultati del test (sì/no), e le foglie una predizione finale (classe o probabilità di classe). Un **regression tree** è analogo, ma i nodi interni testano una singola feature, hanno tipicamente due rami, e le foglie sono nodi terminali che contengono un *valore reale*: nel complesso rappresenta una funzione che approssima un valore reale spezzando lo spazio delle feature in regioni e assegnando un numero fisso a ciascuna.

### GBRT

Il **Gradient Boosting Regression Tree (GBRT)** costruisce un modello forte sommando tanti regression tree deboli, addestrati uno dopo l'altro per correggere gli errori dei precedenti. Il modello finale è una somma pesata di alberi: $s(d) = \sum_{i=1}^{n} w_i\, s_i(d)$, dove ogni $s_i(d)$ è il contributo dell'albero $i$-esimo e $w_i$ il suo peso.

> **Definizione: modello come somma di weak learner**
>
> $$
> F(d) = \sum_i f_i(d)
> $$
>
> dove $F(d)$ è il punteggio finale del documento e ogni $f_i$ è un weak learner (tipicamente un piccolo regression tree). Il boosting costruisce $F$ iterativamente, aggiungendo un $f_i$ alla volta. Ogni $f_i$ è un passo di discesa del gradiente: per minimizzare una loss $L(y,f(d))$, il miglior passo locale è nella direzione del gradiente negativo, $f_i(d) = -\rho_i g_i(d)$, dove $g_i(d)$ è il gradiente della loss rispetto a $f(d)$ calcolato sul modello corrente e $\rho_i$ è lo step scelto per *line search*.

Con loss $L=\frac12 SSE = \frac12\sum_d (y-f(d))^2$, il negativo del gradiente si riduce semplicemente al **residuo**: $-\partial L/\partial f(d) = y-f(d)$, quindi al passo $i$ lo pseudo-residuo è $residuo_i(d) = y - F_{i-1}(d)$. Si costruisce allora il dataset $(d,\, y-F_{i-1}(d))$ e vi si fitta un regression tree $t_i$ che approssima il gradiente: $f_i(d)\approx-\rho_i t_i(d)$. Intuitivamente: dopo il primo albero si ha una predizione $f_1(d)$ con errore $y-f_1(d)$; il secondo albero $f_2$ corregge tale errore, il terzo lo corregge ulteriormente, e così via – man mano che si aggiungono alberi, $F(d)=\sum_i f_i(d)$ si avvicina progressivamente a $y$.

> **Esempio: un passo di GBRT su un piccolo dataset**
>
> Sei istanze (studenti) con feature *Time* e *Project*, target $y$:
>
> | Studente | 2 | 1 | 6 | 4 | 3 |
> | --- | --- | --- | --- | --- | --- |
> | Time | 10 | 20 | 30 | 30 | 40 |
> | Project | 1 | 2 | 1 | 2 | 4 |
> | $y$ | 18 | 30 | 21 | 25 | 30 |
>
> (più lo studente 5, Time=50, Project=3, $y=20$). La predizione iniziale è la media: $F_0=24$. I residui $r_0=y-F_0$ sono quindi $-6,6,-3,1,6,-4$; la loro somma è 0, quindi $score_{root}=0$. Testando lo split `Project`$\le 1$: il ramo sinistro raccoglie gli studenti 2 e 6 (residui $-6,-3$), il ramo destro gli altri quattro (residui $6,1,6,-4$). Gli score dei due rami si calcolano come $(\sum r_0)^2/n$: $score_{left}=(-9)^2/2=40.5$, $score_{right}=(9)^2/4=20.25$; il guadagno dello split è $gain=score_{left}+score_{right}-score_{root}+\gamma$, positivo e quindi conveniente (con $\gamma$ costante di potatura, tipicamente piccola). L'albero continua a espandersi ricorsivamente (es. il ramo destro si divide ulteriormente su `Time`$\le 45$); ogni foglia finale restituisce come output la media dei residui che contiene (qui: $-4.5$, $4.3$, $-3$). Questo singolo albero $f_1$ viene sommato a $F_0$ per ottenere $F_1$, e il ciclo si ripete per l'albero successivo sui nuovi residui.

## Algoritmi Pairwise: RankNet

Gli algoritmi **Pairwise** imparano dalle coppie di documenti che, per una stessa query, sono ordinate (uno è migliore dell'altro). **RankNet** è il capostipite: per una query $q$ prende due documenti $d_i,d_j$ con $y_i>y_j$ (cioè $d_i$ è più rilevante), e tramite la funzione di scoring $F(d)$ (una rete neurale) calcola $o_{ij}=F(d_i)-F(d_j)$.

> **Definizione: RankNet**
>
> RankNet interpreta la differenza $o_{ij}$ come un logit e la trasforma in probabilità che $d_i$ sia migliore di $d_j$:
>
> $$
> P_{ij} = \frac{e^{o_{ij}}}{1+e^{o_{ij}}} = \frac{1}{1+e^{-o_{ij}}}
> $$
>
> ($P_{ij}\approx1$ se il modello è convinto che $d_i$ sia migliore, $\approx0$ altrimenti). Dai giudizi si conosce la verità, riassunta nel **target** $T_{ij}=1$ (se $d_i$ deve stare sopra $d_j$). La **Cross-Entropy Loss** misura quanto $P_{ij}$ è lontano dal target:
>
> $$
> C_{ij} = -T_{ij}\log P_{ij} - (1-T_{ij})\log(1-P_{ij})
> $$
>
> Considerando solo le coppie con $y_i>y_j$ (dunque $T_{ij}=1$), la loss si semplifica in $C_{ij}=\log(1+e^{-o_{ij}})$: se il modello ordina correttamente ($o_{ij}\to+\infty$) il costo tende a 0; se sbaglia ($o_{ij}\to-\infty$) esplode verso infinito. Essendo $C_{ij}$ differenziabile rispetto ai pesi della rete, RankNet si addestra con back-propagation.

A tempo di ranking, una volta addestrato RankNet, per ogni documento candidato si calcola soltanto $F(d)$ e si ordina in base a questo: l'informazione “pairwise” (le coppie, le $P_{ij}$) serve solo in fase di training, non a runtime. RankNet performa meglio degli altri algoritmi pairwise, ma il suo costo non è ben correlato con la qualità NDCG – un limite che motiva l'approccio Listwise.

## Algoritmi Listwise: LambdaMART

Gli algoritmi **Listwise** guardano l'intera lista di documenti per una query e cercano di ottimizzare direttamente una metrica di ranking (es. NDCG): valutano la qualità della lista con la metrica, costruiscono una loss che minimizzata tende a massimizzarla, e aggiornano i parametri col gradiente (o un surrogato).

Il problema è che GBRT, a ogni iterazione, ha bisogno di un gradiente $g_i$ per ogni documento $d_i$. **LambdaMART** costruisce questi gradienti in modo furbo, combinando l'idea pairwise di RankNet con la variazione della metrica listwise.

> **Definizione: Lambda Gradients (LambdaMART)**
>
> Per ogni coppia $(d_i,d_j)$ con $y_i\ge y_j$, posto $o_{ij}=F(d_i)-F(d_j)$ come in RankNet, si definisce:
>
> $$
> \lambda_{ij} = \frac{1}{1+e^{o_{ij}}}\, |\Delta NDCG|
> $$
>
> Il fattore $\frac{1}{1+e^{o_{ij}}}$ viene dalla derivata del costo di RankNet (grande se l'ordinamento è sbagliato, tendente a 0 se è giusto); $|\Delta NDCG|$ è la variazione di NDCG che si otterrebbe scambiando le posizioni di $d_i$ e $d_j$ (grande se lo scambio peserebbe molto sulla metrica, quindi soprattutto nelle prime posizioni). Vale $\lambda_{ij}=-\lambda_{ji}$. Il gradiente totale per il documento $d_i$ si ottiene sommando i contributi su tutte le coppie che lo coinvolgono:
>
> $$
> g_i = \lambda_i = \sum_{y_i>y_j}\lambda_{ij} - \sum_{y_i<y_j}\lambda_{ij}
> $$
>

L'integrazione in GBRT è diretta: per ogni documento $d_i$ si calcola il suo lambda-gradient $\lambda_i$ e si costruisce il dataset $(\text{feature}(d_i),\,\lambda_i)$; l'algoritmo è un GBRT che, invece dei residui $y-F(d)$ dei GBRT “normali”, usa questi lambda-gradients come pseudo-response. A ogni iterazione si adatta un regression tree per approssimare $\lambda_i$: aggiornando il modello in questa direzione si migliora direttamente l'NDCG, pur sfruttando la meccanica dei GBRT. LambdaMART è tuttora lo stato dell'arte per il ranking con alberi.

## Multi-Stage Ranking

### Single-Stage Ranking

Il **Single-Stage Ranking** usa un unico ranker per passare dai documenti indicizzati alla lista finale, senza uno stadio di re-ranking successivo. Richiede però di applicare il modello appreso a *tutti* i documenti che matchano la query e di generarne tutte le feature: è quindi un compromesso insostenibile: o è computazionalmente costoso (feature altamente discriminanti) o è economico ma con feature poco discriminanti.

### Two-Stage Ranking

Il **Two-Stage Ranking** usa prima uno stadio veloce che genera candidati, poi uno stadio costoso e intelligente che li riordina.
- **Primo stadio (Base Ranker / Candidate Generation)**: interroga direttamente l'indice con un ranker semplice e velocissimo (BM25, VSM tf-idf o varianti leggere), per ridurre milioni/miliardi di documenti ai top-$N$ candidati (es. 1000).
- **Secondo stadio (Top Ranker / Re-Ranking)**: lavora solo sugli $N$ candidati; qui entra il modello LtR potente (GBRT/LambdaMART, RankNet, modelli neurali) che, con un vettore di feature ricco per ogni coppia, produce $s_{LTR}(q,d)$ e seleziona i top-$K$ (es. 10–20) da mostrare.

### Multi-Stage Ranking

Il **Multi-Stage Ranking** generalizza il two-stage a una sequenza di più stadi, ciascuno con un modello, un insieme di feature e un ruolo specifico: gli stadi iniziali sono **recall-oriented** (modelli semplici ed efficienti, es. BM25, per filtrare l'intero indice senza scartare troppo presto i rilevanti); gli stadi intermedi sono **precision-oriented** (modelli LtR sofisticati come GBRT/LambdaMART su meno documenti, con feature più ricche); gli stadi finali possono essere **context-aware** (feature che dipendono dall'intero insieme corrente – posizione relativa, statistiche di lista, similarità di topic – per un ultimo riordinamento fine). Così i modelli più costosi girano solo su pochi documenti, mentre gli stadi iniziali garantiscono che i documenti rilevanti non vengano persi.

![Cascade: lo Stage 1 (recall-oriented) filtra i candidati sull'intero indice, lo Stage 2 (precision-oriented) ri-ordina i top-$N$ sopravvissuti.](Files%20and%20Images/fig_pipeline_stages.png)

*Cascade: lo Stage 1 (recall-oriented) filtra i candidati sull'intero indice, lo Stage 2 (precision-oriented) ri-ordina i top-$N$ sopravvissuti.*

Il dimensionamento della pipeline dipende da tre **trade-off**: il **Feature Computation Trade-off** (feature statiche pre-calcolate ed economiche vs feature query-dependent costose ma discriminanti), il **Number of Matching Candidates Trade-off** (una vasca ampia di candidati dà qualità ma costa, una scrematura aggressiva è veloce ma rischia di perdere buoni documenti) e il **Model Complexity Trade-off** (modello lineare veloce vs ensemble/rete più accurati ma costosi). Sperimentalmente, pipeline a 3 stadi che restringono gradualmente il campo (es. 2500 candidati al primo passaggio, 700 all'intermedio) ottengono i risultati migliori.

![Impatto della scelta del modello sull'architettura: dal training sui dati offline al modello appreso applicato online a campioni con feature per produrre i risultati.](Files%20and%20Images/fig_cascade_architecture.png)

*Impatto della scelta del modello sull'architettura: dal training sui dati offline al modello appreso applicato online a campioni con feature per produrre i risultati.*

## Ottimizzazioni di efficienza per la LtR ad alberi

> **Osservazione: nota sulle fonti**
>
> Le tecniche di questa sezione appartengono al filone di ricerca sull'*efficienza della LtR ad alberi*, trattato nelle slide del corso (deck sui trade-off efficienza/efficacia) ma non nel testo di riferimento *informationretrieval_notes*. Sono incluse per completezza. L'efficienza è affrontata lungo tre direttrici: ottimizzare l'apprendimento stesso, approssimare il calcolo del punteggio con cascate/uscite anticipate, e velocizzare l'attraversamento dei singoli alberi (QuickScorer, Capitolo *Training e Inferenza Efficiente di Alberi*).

> **Definizione: Learning to Efficiently Rank (Wang et al.) e Cost-sensitive Tree Induction (Asadi e Lin)**
>
> **Wang et al.** introducono una funzione di costo che ottimizza direttamente una metrica di trade-off efficienza-efficacia (EET), con penalità di costo costante/a gradino/esponenziale, su ranking function lineari. **Asadi e Lin** puntano invece ad alberi compatti, bassi e bilanciati (più veloci in predizione): modificano il criterio di split durante l'induzione (split a guadagno massimo solo se non aumenta la profondità) oppure applicano pruning durante il boosting; sperimentalmente il pruning riduce la latenza di predizione del 40% con perdita di NDCG minima.

> **Definizione: CLEAVER e X-CLEAVER**
>
> **CLEAVER** è una metodologia di *post-processing* che pota e ri-pesa gli alberi di un ensemble già addestrato (MART/LambdaMART), tramite una ricerca greedy in linea (*line search*) sui pesi. Diverse strategie di potatura (random, last, skip, low weights, score loss, quality loss): la strategia *quality loss* mantiene l'efficacia originale con appena il 20% degli alberi. **X-CLEAVER** integra potatura e ri-pesatura direttamente *durante* il gradient boosting, producendo foreste ancora più compatte a parità di qualità.

> **Definizione: DART e X-DART**
>
> **DART** applica il *dropout* delle reti neurali al Multiple Additive Regression Trees (MART): a ogni iterazione un sottoinsieme casuale di alberi già costruiti viene “silenziato”, contrastando l'*over-specialization*; un passo di normalizzazione evita l'overshooting. **X-DART** unisce DART e potatura, rimuovendo definitivamente gli alberi silenziati non necessari (fino al 40% di alberi in meno a parità di efficacia) e riducendo l'overfitting.

> **Definizione: Early Exit a soglie (Cambazoglu et al.) e Ranking Distillation**
>
> **Early Exit**: poiché per ogni query pochi documenti sono rilevanti e gli utenti guardano solo le prime posizioni, si interrompe anticipatamente l'attraversamento degli alberi con soglie di **Score**, **Capacity** (max punteggio residuo), **Rank** (posizione stimata) e **Proximity** (vicinanza alla soglia top-$k$), fino a 4$\times$ più veloce senza perdita di qualità. **Ranking Distillation**: si addestra una rete neurale (student, veloce in inferenza) ad approssimare gli output di un LambdaMART già addestrato (teacher), arricchendo il dataset attorno agli split point degli alberi – ereditando gran parte dell'accuratezza con la velocità delle reti neurali su GPU.

# Training e Inferenza Efficiente di Alberi

## Gradient Boosting Machine (GBM)

Il **GBM** combina molti piccoli alberi aggiunti uno alla volta, ciascuno addestrato per ridurre gli errori dei precedenti. È un algoritmo di **Boosting**: non usa un unico modello complesso, ma tanti *weak learner* (modelli deboli, presi da soli con capacità predittiva limitata ma veloci e semplici), dove ogni nuovo modello è addestrato sugli errori dei precedenti per correggerli. Nel GBM i weak learner sono alberi decisionali poco profondi, e il modello finale è la loro somma:

$$
F(x) = \sum_{i=0}^{M} F_i(x)
$$

Il GBM serve per regressione, classificazione e Learning to Rank (impara una funzione di ranking come farebbe per la regressione). L'idea di base è di Breiman (1997), aggiornata da Friedman (1999), ed è oggi implementata in librerie ottimizzate come XGBoost e LightGBM.

> **Osservazione: dettagli del training**
>
> La meccanica iterativa (predizione iniziale $F_0=$ media dei target, pseudo-residui $r_{i,m}=y_i-F_{m-1}(x_i)$ per l'MSE, fit di un albero sui residui, aggiornamento $F_m=F_{m-1}+\alpha\gamma_{j,m}$) è già stata sviluppata in dettaglio nel Capitolo *Learning to Rank II*, con l'esempio numerico dei sei studenti. Qui l'attenzione si sposta sull'**efficienza** di training e inferenza di questi ensemble.

## LtR con gruppi di Regression Trees

Il modello LtR è una **foresta di alberi**: un gruppo di weak learner (decision tree) dove ognuno contribuisce con uno score parziale, e lo score di un documento è la somma di tutti gli score parziali, $s(d)=\sum_{i=1}^{n} s_i(d)$. Il rovescio della medaglia è che, a **scoring time**, per ogni documento si devono processare *tutti* gli alberi. Con numeri realistici – $M$ tra 1K e 20K alberi, da 16 a 512 foglie ciascuno, 100–2000 feature coinvolte – il costo è enorme.

> **Osservazione: perché anche l'inferenza è un collo di bottiglia**
>
> Supponendo 1000 documenti candidati per query, un modello di 3000 alberi di profondità 10: servono $3000\times10=30\,000$ test per documento, cioè $30\,000\times1000=30$ milioni di test per una singola query. Anche un modello già addestrato e ottimo in accuratezza resta insostenibile se attraversato nel modo ingenuo.

## Valutazione di un Decision Tree

Per capire come velocizzare lo scoring, si parte dal come un singolo albero valuta un documento. Dato il vettore di feature della coppia $(q,d)$, $x=(F_1,\dots,F_8)$ (ogni $F_i$ è una caratteristica: BM25, PageRank, lunghezza, …), l'albero ha nodi interni etichettati $\langle\text{soglia}, F_i\rangle$ e foglie con valori numerici. Attraversando: a ogni nodo si confronta $F_i$ con la soglia, se $F_i\le\text{soglia}$ si va a sinistra altrimenti a destra; arrivati a una foglia, quel numero è lo score restituito dall'albero.

> **Esempio: attraversamento di un albero**
>
> Radice $\langle 50.1, F_4\rangle$: con $F_4=43.9$, $43.9\le50.1$ è vero $\to$ ramo sinistro. Nodo $\langle 10.1, F_1\rangle$: con $F_1=13.1$, $13.1\le10.1$ è falso $\to$ ramo destro. Nodo successivo su $F_3$: confronto vero $\to$ sinistra $\to$ foglia di uscita (*exit leaf*) con valore $2.0$. Lo score della coppia $(q,d)$ per questo albero è $2.0$, che viene sommato al totale del documento.

### QuickScorer

**QuickScorer (QS)** valuta una foresta di alberi molto più in fretta della visita classica “root-to-leaf”.

> **Definizione: la baseline naive e i suoi limiti**
>
> La visita classica, per ogni documento e ogni albero, parte dalla radice, a ogni nodo fa il test $x[f]\le\text{soglia}?$ e scende a sinistra/destra. Rappresentando ogni nodo come oggetto con feature id, soglia e puntatori ai figli (una catena di `if-then-else`), si pagano tre problemi hardware: (1) si paga **sempre** l'intera profondità dell'albero; (2) alto tasso di **branch misprediction** (la CPU non prevede condizionali dipendenti dai dati); (3) **basso cache hit ratio** (i nodi figli puntatore-based non sono contigui). Tecniche intermedie come VPred o Struct+ attenuano ma non eliminano il problema.

QuickScorer ragiona in modo diverso: per un dato vettore $x$, ogni nodo interno è **True** se il test $x[f]\le\text{soglia}$ è vero, **False** altrimenti. Non interessa più quale strada percorrere, ma solo quali foglie sono compatibili con i nodi risultati *False*. Ogni albero con $\Lambda$ foglie è codificato così: alle foglie si dà un indice $0,\dots,\Lambda-1$; per ogni nodo interno si pre-calcola una **maschera** di $\Lambda$ bit (0 dove le foglie diventano irraggiungibili se quel nodo è False, 1 altrove). Si parte da un bitvector `leafidx` tutto a 1, si scorrono i nodi False e per ognuno si applica $\texttt{leafidx} \leftarrow \texttt{leafidx} \wedge \text{mask(node)}$, azzerando le foglie rese impossibili; alla fine resta a 1 una sola foglia – la exit leaf – e la posizione del primo 1 dà il valore da leggere. Poiché l'AND logico è commutativo, il risultato è **insensibile all'ordine** di elaborazione dei nodi False.

![False Nodes' Masks: l'AND logico delle maschere associate ai nodi False identifica direttamente la foglia di uscita (*exit leaf*) per il documento.](Files%20and%20Images/fig_quickscorer.png)

*False Nodes' Masks: l'AND logico delle maschere associate ai nodi False identifica direttamente la foglia di uscita (*exit leaf*) per il documento.*

**Attraversamento interleaved dell'intera foresta.** Offline, per ogni feature $f_\varphi$ si costruisce una lista $N_\varphi$ di triple $(\gamma,\text{mask},h)$ – soglia, maschera, indice dell'albero – ordinata per $\gamma$ crescente. Per un documento $x$: si inizializza `leafidx` a $1\dots1$ per tutti gli alberi; poi per ogni feature $f_\varphi$ si scorre $N_\varphi$ in ordine di soglia finché $x[\varphi]>\gamma$ (il test è False, si applica l'AND della maschera), fermandosi appena $x[\varphi]\le\gamma$ (da lì i test sono True). Infine, per ogni albero si legge la prima foglia a 1 e si sommano i valori. Questa disposizione dei dati per feature in array contigui ordinati (invece che come alberi separati) garantisce **alto cache hit ratio** e **basso branch misprediction**, ed elabora blocchi di documenti simultaneamente sfruttando registri SIMD.

> **Osservazione: risultati sperimentali**
>
> Su dataset standard (MSN-1, Y!S1), confrontando il tempo di scoring per documento (microsecondi) tra QuickScorer (QS), VPred, If-Then-Else e Struct+, QS è sistematicamente il più veloce a parità di alberi e profondità: già a 1000 alberi con 8 foglie, QS impiega $\sim$2–4$\mu$s contro $\sim$8–10$\mu$s di VPred/If-Then-Else e $\sim$20$\mu$s di Struct+ (speedup 3–10$\times$); il vantaggio cresce all'aumentare del numero di alberi e della profondità, confermando che eliminare i salti condizionali paga sempre di più quanto più grande è l'ensemble.

## XGBoost

**XGBoost** è una specifica implementazione, molto ottimizzata, di GBM. Proposta da Tianqi Chen e Carlos Guestrin (2014), è diventata l'algoritmo di riferimento per molti team vincenti nelle competizioni di ML a metà anni 2010. Migliora le implementazioni esistenti per scalare con la quantità di dati tramite: approcci **approssimati** per la ricerca dello split, **elaborazione parallela** (sulla ricerca dei punti di split, non sulla costruzione degli alberi, che resta sequenziale), accesso ai dati **cache-aware**, e tecniche di **regolarizzazione e pruning** durante il branching.

### Exact Greedy for Split Finding

> **Definizione: Exact Greedy Algorithm**
>
> Il cuore del training è trovare, per ogni nodo, la coppia $(f,t)$ (feature, soglia) che massimizza la riduzione della loss (il *gain* dello split). È *greedy* perché sceglie il miglior split locale senza guardare al futuro dell'albero, ed *exact* perché per ogni feature considera *tutti* i possibili punti di split (tra i valori ordinati), non un campionamento. Per ogni feature si ordinano gli esempi del nodo e si scorre la lista valutando i fino a $N-1$ punti candidati: il costo per nodo è quindi proporzionale a $O(\text{feature}\times\text{data point})$.

> **Osservazione: colli di bottiglia dello split finding**
>
> L'Exact Greedy è matematicamente ottimale ma oneroso, per tre motivi: (1) il **costo di ordinamento** dei valori a ogni nodo; (2) i **cache miss**, poiché l'accesso ai gradienti corrispondenti agli indici ordinati spesso non è contiguo in memoria; (3) la **frammentazione dei dati** man mano che l'albero cresce, che riduce l'efficacia di SIMD e prefetching. Le tecniche seguenti (Histogram, GOSS, EFB) attaccano rispettivamente il numero di valori per feature, il numero di istanze $N$ e il numero di feature $M$.

### Histogram-Based Split Finding

> **Definizione: Histogram-based**
>
> Invece di valutare tutti gli $N$ valori continui di una feature, questi vengono discretizzati in un numero fisso di bin (aggregando valori consecutivi), costruendo un istogramma; lo split si cerca solo sui confini dei bin. La complessità passa da $O(\text{data}\times\text{feature})$ a $O(\text{bin}\times\text{feature})$, con drastica riduzione di memoria e aumento di velocità, e una perdita di precisione spesso trascurabile (che può fungere da regolarizzazione).

### Numero di Bins vs Training Time

Fissato un parametro $b$ (bit), i valori di ogni feature vengono quantizzati in $2^b$ bin. Ciò permette di addestrare dataset enormi con un footprint di memoria contenuto: il default è 255 bin (16 su GPU). Meno bin significa training più veloce e meno memoria, ma una discretizzazione più grossolana.

### LightGBM: GOSS ed EFB

**LightGBM** (Microsoft, 2016) processa dataset enormi ancora più velocemente di XGBoost. Pur basandosi sull'histogram-based split finding, osserva che costruire e aggiornare gli istogrammi resta proporzionale a $\text{\#feature}\times\text{\#data points}$: agisce quindi su entrambi i fronti, riducendo sia il numero di istanze (GOSS) sia il numero di feature (EFB).

> **Definizione: GOSS (Gradient-based One-Side Sampling)**
>
> Le istanze con gradiente piccolo sono già ben apprese e contribuiscono poco all'information gain; quelle con gradiente grande necessitano più attenzione. GOSS mantiene *tutte* le istanze con gradiente grande (top $a\times100\%$) e campiona casualmente una piccola porzione di quelle con gradiente piccolo (top $b\times100\%$). Per non alterare la distribuzione originale, i gradienti delle istanze campionate a gradiente piccolo vengono amplificati per un fattore $(1-a)/b$ nel calcolo dell'information gain. Riducendo le istanze processate da $N$ a una frazione $a+b$, il costo per iterazione cala proporzionalmente con accuratezza paragonabile al training completo.

> **Definizione: EFB (Exclusive Feature Bundling)**
>
> In dataset ad alta dimensionalità e sparsi, molte feature sono **mutuamente esclusive** (mai non-nulle contemporaneamente, come nel one-hot encoding). EFB le raggruppa (*bundle*) in un'unica feature densa, trattando il problema come colorazione di grafo (feature = nodi, co-occorrenza di valori non nulli = conflitti/archi): trovare il numero minimo di bundle è NP-hard, quindi si usa un'euristica greedy. La complessità della costruzione degli istogrammi passa da $O(\text{data}\times\text{feature})$ a $O(\text{data}\times\text{bundle})$, con numero di bundle $\ll$ feature originali.

> **Esempio: offset nel bundling**
>
> Feature A con range $[0,10)$ e feature B con range $[0,20)$, mutuamente esclusive: nel bundle, B viene rimappata su $[10,30)$ tramite un offset di 10, così un unico valore nel bundle risale univocamente al valore originale corretto (mapping lossless).

# Neural Information Retrieval

## Modelli di Linguaggio Statistici

> **Definizione: Statistical Language Model**
>
> Un modello di linguaggio statistico è una distribuzione di probabilità $P$ su sequenze di termini. Dato un documento $d=w_1w_2w_3\dots$, per la regola della catena:
>
> $$
> P(d) = P(w_1)P(w_2|w_1)P(w_3|w_1w_2)\cdots
> $$
>
> Questa formulazione esatta non richiede assunzioni ma è impraticabile: servirebbe stimare la probabilità di ogni possibile sequenza del linguaggio.

> **Definizione: Unigram Model**
>
> Assume indipendenza statistica tra le parole: $P(d) = \prod_i P(w_i)$. È il modello alla base del classificatore Naive Bayes. Si lavora tipicamente con i logaritmi delle probabilità per restare in uno spazio lineare additivo: $\log P(d) = \sum_i \log P(w_i)$. Lo **smoothing** (es. aggiungere 1 a tutte le frequenze, add-one/Laplace smoothing) evita probabilità nulle per termini mai osservati.

> **Definizione: Bigram / N-gram Model**
>
> Assume che ogni parola dipenda solo dalla precedente (catena di Markov di ordine 1): $P(d) = \prod_i P(w_i|w_{i-1})$, generalizzabile a n-grammi di ordine superiore. All'aumentare di $n$ il modello cattura più regolarità semantiche, ma diventa meno robusto statisticamente: molte sequenze di $n$ parole non compaiono mai nel training set (sparsità), generando un trade-off tra memorizzazione e generalizzazione.

> **Osservazione: dai modelli statistici ai modelli neurali**
>
> I modelli n-gram sono alla base di un terzo paradigma classico di ranking in IR (accanto a Vector Space Model e BM25): il *Language Modeling for IR*, dove un documento è ritenuto rilevante se il suo modello di linguaggio assegna un'alta probabilità alla query. Il limite principale è la sparsità: la stima delle probabilità condizionate diventa inaffidabile all'aumentare di $n$ o della dimensione del vocabolario. I modelli neurali (Word2Vec e successori) superano questo limite sostituendo il conteggio di occorrenze con rappresentazioni dense apprese, capaci di generalizzare a sequenze mai osservate durante il training.

## Word2Vec e FastText

I modelli di linguaggio neurali superano i limiti delle rappresentazioni one-hot (parole come simboli atomici ortogonali) introducendo il **word embedding**: rappresentazione vettoriale densa e continua dove parole semanticamente simili sono vicine nello spazio geometrico.

> **Definizione: Word2Vec**
>
> Famiglia di architetture predittive basata sull'ipotesi distribuzionale (il significato di una parola è determinato dal contesto in cui appare):
> - **CBOW** (Continuous Bag-of-Words): predice la parola target dalla media dei vettori delle parole di contesto. Veloce, buone prestazioni su termini frequenti.
> - **Skip-gram**: predice le parole di contesto data la parola target. Più costoso, gestisce meglio i termini rari.
>

> **Osservazione: risultato scientifico principale di Word2Vec**
>
> Word2Vec cattura relazioni semantiche e sintattiche come operazioni algebriche tra vettori: $\vec{king} - \vec{man} + \vec{woman} \approx \vec{queen}$, mostrando che genere e altre relazioni latenti sono codificate come direzioni nello spazio vettoriale. Ciò supera i limiti delle rappresentazioni sparse (one-hot), incapaci di misurare similarità tra termini diversi.

Limite di Word2Vec: incapacità di gestire parole fuori vocabolario (**OOV**) e assenza di comprensione morfologica.

> **Definizione: FastText**
>
> Rappresenta ogni parola come somma dei vettori dei suoi n-grammi di caratteri (subword information). Es. “apple” $\to$ `<ap, app, ppl, ple, le>`. Permette embedding per parole mai viste o con errori di battitura, basandosi su radici e suffissi.

## Document Embeddings: Doc2Vec e Pooling

Il **pooling** (media o massimo) dei vettori Word2Vec delle parole di un documento è un approccio semplice ma perde l'informazione sull'ordine e la sintassi.

> **Definizione: Doc2Vec (Paragraph Vector)**
>
> Estende Word2Vec inserendo un vettore aggiuntivo che rappresenta l'ID del documento/paragrafo. Nell'architettura **PV-DM**, il vettore del documento agisce da memoria globale che, insieme ai vettori di contesto, contribuisce a predire la parola successiva, catturando la semantica complessiva in un vettore di dimensione fissa.

## Transformer e BERT

I **Transformer** generano embedding *contestuali*: la rappresentazione di una parola cambia in base al suo utilizzo nella frase (gestisce la polisemia). Il cuore è il meccanismo di **Self-Attention**:

$$
\text{Attention}(Q,K,V) = \text{softmax}\left(\frac{QK^T}{\sqrt{d_k}}\right)V
$$

Il fattore $\sqrt{d_k}$ scala i prodotti scalari per evitare gradienti troppo piccoli. La **Multi-headed Attention** applica il meccanismo in parallelo più volte (teste), permettendo di focalizzarsi simultaneamente su diversi sottospazi (es. relazioni sintattiche in una testa, semantiche in un'altra).

**BERT** (Bidirectional Encoder Representations from Transformers) usa una pila di encoder Transformer addestrati su due task: **Masked Language Modeling** (MLM), che oscura casualmente alcune parole e chiede al modello di predirle usando il contesto sia sinistro che destro, e **Next Sentence Prediction** (NSP), che presenta coppie di frasi $(A,B)$ e chiede al modello di predire se $B$ segue realmente $A$ nel testo originale o è stata scelta a caso – utile per compiti che richiedono relazioni tra frasi (QA, NLI). Insieme, i due task producono rappresentazioni profonde e bidirezionali, ideali per il ranking.

BERT usa tre token speciali: **[CLS]**, posto all'inizio della sequenza, il cui embedding finale riassume l'intera sequenza (usato in IR come score di rilevanza); **[MASK]**, che marca le parole da predire nell'MLM; **[SEP]**, che separa frasi o segmenti (es. `[CLS] q [SEP] d [SEP]` per rappresentare query e documento in un Cross-Encoder).

![Architettura di BERT: pila di encoder Transformer bidirezionali sugli embedding di token, segmento e posizione.](Files%20and%20Images/fig_bert_architecture.png)

*Architettura di BERT: pila di encoder Transformer bidirezionali sugli embedding di token, segmento e posizione.*

## Neural Approaches for IR

I metodi neurali per l'IR si dividono in due famiglie, distinte dal modo in cui query e documento vengono fatti interagire per calcolare lo score di rilevanza.

### Interaction-based: MonoBERT

Nei metodi basati sull'**interazione**, query e documento vengono messi insieme e passati *in coppia* al modello neurale, che può guardare tutte le interazioni termine-termine (ogni parola della query con ogni parola del documento). L'archetipo è **MonoBERT**: l'input è la sequenza `[CLS] q [SEP] d [SEP]` e l'output è l'embedding finale del token `[CLS]`, proiettato in uno scalare = rilevanza$(q,d)$. Grazie alla full self-attention, ogni token della query interagisce con ogni token del documento a tutti i livelli, garantendo accuratezza superiore, ma rendendo *impossibile* l'indicizzazione vettoriale pre-calcolata (query e documento non sono separabili). I Cross-Encoder sono quindi usati solo nell'ultimo stadio di re-ranking, su un piccolo set di candidati (top-100/1000) recuperati da un modello più efficiente (es. BM25).

Il fine-tuning di MonoBERT combina un modello *Point-wise* con addestramento *Pair-wise*: per ogni query si calcola lo score $s_j$ di documenti positivi e negativi, minimizzando la cross-entropy

$$
L_{mono} = -\sum_{j\in J_{pos}}\log(s_j) - \sum_{j\in J_{neg}}\log(1-s_j)
$$

che spinge gli score dei positivi verso 1 e dei negativi verso 0. Con appena 100.000 coppie di training (0.3% dei dati completi, da 10.000 query $\times$ 10 passaggi via BM25), MonoBERT già supera i modelli neurali precedenti (KNRM, Conv-KNRM) e le baseline BM25 su MS MARCO (MRR@10) e TREC-CAR (MAP).

![MonoBERT: query e documento sono concatenati in un unico input; l'embedding contestuale del token [CLS] fornisce direttamente lo score di rilevanza.](Files%20and%20Images/fig_monobert.png)

*MonoBERT: query e documento sono concatenati in un unico input; l'embedding contestuale del token [CLS] fornisce direttamente lo score di rilevanza.*

### Representation-based: Bi-Encoder

Nei metodi basati sulla **rappresentazione** (*Bi-Encoder* / *Dual Encoder*), si impara una funzione neurale che mappa la query in un vettore denso $v_q$ e il documento in un vettore denso $v_d$, e la rilevanza è la loro similarità geometrica $\text{sim}(v_q,v_d)$ (prodotto scalare o coseno). Il vantaggio decisivo è il disaccoppiamento: **offline** si calcolano gli embedding di tutti i documenti e li si salvano in un indice vettoriale; **online**, all'arrivo della query, si calcola solo $v_q$ e si cercano i documenti con $v_d$ più simili – una ricerca kNN nello spazio vettoriale, tipicamente approssimata (**ANN**, Capitolo *Approximate Nearest Neighbor (AkNN) Search*) per gestire collezioni massive. È l'opposto del Cross-Encoder: meno accurato (nessuna interazione fine query-documento) ma enormemente più efficiente, perché il grosso del lavoro è precalcolato.

## Rappresentazioni dense

Nelle rappresentazioni dense si cerca un modello che, data una query $q$, un documento rilevante $d^+$ e uno o più documenti non rilevanti $d^-$, produca embedding tali che $q$ sia vicino a $d^+$ e lontano da $d^-$. Si lavora con coppie o triple $(q,d^+,d^-)$; le loss catturano distanze *relative* tra esempi, non valori assoluti.

### Loss e scelta dei negativi

> **Definizione: Triplet Loss**
>
> Impone che la distanza tra query e positivo tenda a zero e quella tra query e negativo superi un margine $m$ prefissato:
>
> $$
> \text{dist}(q,d^+) \to 0, \qquad \text{dist}(q,d^-) > m
> $$
>
> Penalizza i casi in cui il negativo è troppo vicino alla query rispetto al positivo.

I positivi $d^+$ sono noti (dai giudizi di rilevanza, dai click); il vero problema è scegliere i negativi $d^-$. Il random sampling produce spesso *easy negatives* (troppo lontani da $q$, facili, insegnano poco); si usano anche selezione in-batch/cross-batch (si riusano i documenti del batch come negativi per altre query). Si distinguono tre livelli: **Easy Negatives** (chiaramente irrilevanti, lontani da $q$), **Semi-Hard Negatives** (un po' vicini alla query ma meno del positivo) e **Hard Negatives** (molto vicini alla query pur non rilevanti – i più difficili e utili). Per gli Hard Negatives ci sono due strategie: **static sampling** (pre-calcolati una volta, es. con BM25, e fissati) e **dynamic sampling** (a ogni passo di training si usa il modello corrente per trovare i documenti irrilevanti ma top-ranked, che diventano i nuovi hard negatives).

### Le tre famiglie: single-vector, multi-vector, sparso appreso

> **Definizione: Dense Single-Vector (es. ANCE)**
>
> L'intero contenuto semantico del documento è compresso in un unico vettore denso a dimensione fissa (es. 768, da `[CLS]` o mean pooling). **Pro**: massima efficienza in memoria e latenza, ricerca NN rapidissima. **Contro**: comprimere un passaggio lungo in un solo vettore perde informazioni fini, rendendo difficile il matching preciso. **ANCE** usa *hard negative dinamici* recuperati dall'indice durante il training, correggendo la discrepanza tra distribuzione vista in training e in inferenza.

> **Definizione: Dense Multi-Vector (ColBERT)**
>
> Il documento è rappresentato come una sequenza di vettori (uno per token), non un singolo punto. La similarità si calcola con **Late Interaction**: per ogni token di query si prende la similarità massima con i token del documento (*MaxSim*), poi si sommano i massimi. **ColBERT** è l'esempio canonico: gli embedding dei token dei documenti si precalcolano offline e si indicizzano in un indice ANN. **Pro**: massima efficacia tra i representation-based, si avvicina ai Cross-Encoder. **Contro**: costi di spazio proibitivi (centinaia di vettori per documento) e latenza superiore.

![All-to-all Interaction (Cross-Encoder, in alto) vs Late Interaction di ColBERT (in basso): quest'ultima calcola MaxSim tra ogni token di query e i token del documento, poi somma i massimi.](Files%20and%20Images/fig_colbert.png)

*All-to-all Interaction (Cross-Encoder, in alto) vs Late Interaction di ColBERT (in basso): quest'ultima calcola MaxSim tra ogni token di query e i token del documento, poi somma i massimi.*

> **Definizione: Retrieval Sparso Appreso (es. SPLADE)**
>
> Proietta il testo in vettori sparsi ad altissima dimensionalità (dimensione del vocabolario), dove i pesi non sono frequenze (TF-IDF) ma valori appresi. **SPLADE** usa l'output MLM di BERT per espandere il documento con termini semanticamente correlati anche se assenti dal testo, con regolarizzazione $L_1$ per la sparsità. **Pro**: interpretabile (si leggono i termini “accesi”), compatibile con l'inverted index maturo, buona generalizzazione zero-shot. **Contro**: l'espansione allunga le posting list, richiedendo pruning aggressivo (Seismic, Capitolo *Approximate Nearest Neighbor (AkNN) Search*).

## Efficienza dei Cross-Encoder Neurali

I Transformer profondi (BERT, GPT, …) offrono le prestazioni migliori in IR neurale ma sono computazionalmente enormi: in circa un anno si è passati da 110M parametri (GPT) a 8.3 miliardi (Megatron-LM) fino a 175 miliardi (GPT-3). Un motore di ricerca deve però rispondere in centinaia di millisecondi a migliaia di query al secondo: applicare un Cross-Encoder completo (MonoBERT) a ogni coppia query-documento candidata è troppo costoso. Le tecniche di ottimizzazione seguono due direzioni principali: **de-coupled interaction** (separare il più possibile il calcolo su query e documento) e tecniche generiche di compressione del modello (*knowledge distillation*, *quantization*, *early exit*).

> **Definizione: PreTTR – Precomputing Term Representations**
>
> Nei primi $L-1$ layer di un Transformer, query e documento non hanno ancora bisogno di “vedersi” a vicenda: le interazioni rilevanti per il ranking emergono soprattutto negli ultimi layer. PreTTR sfrutta questa osservazione:
> 1. **Offline**: ogni documento viene processato da solo nei primi $L-1$ layer di BERT, salvando le rappresentazioni dei suoi token.
> 2. **Online**: la query viene processata negli stessi $L-1$ layer una sola volta per query.
> 3. **Ultimo layer**: si concatenano le rappresentazioni precalcolate del documento con quelle appena calcolate della query e si esegue solo l'ultimo layer con self-attention completa, ottenendo lo score dal token `[CLS]` come in MonoBERT.
>
> Il costo per documento si riduce drasticamente, perché i primi $L-1$ layer non vengono ricalcolati a ogni query. Lo svantaggio è lo spazio necessario per memorizzare le rappresentazioni di tutti i token di tutti i documenti, mitigato proiettandole in uno spazio a dimensionalità ridotta (con una piccola decompressione prima dell'ultimo layer): sperimentalmente, comprimere le rappresentazioni dell'83% (da 768 a 128 dimensioni) e ritardare il *join layer* fino al livello 11 mantiene P@20 quasi invariato (da 0.346 a 0.337 su WebTrack) rispetto a BERT base non ottimizzato. Lo speedup cresce con la profondità a cui avviene il join: unendo query e documento al livello 8 si ottiene $3.5\times$, al livello 11 fino a $42\times$ rispetto a MonoBERT completo.

> **Definizione: EPIC – Expansion via Prediction of Importance with Contextualization**
>
> Punta a minimizzare il costo di inferenza mantenendo interpretabilità, tramite due idee: (1) lo score è un semplice prodotto scalare, sfruttando moltiplicazioni di matrici efficienti; (2) EPIC opera come re-ranker su un piccolo insieme di candidati già filtrati (es. da BM25), nel classico schema a due stadi. Per la query costruisce un vettore di *importanza* appresa (non manuale); per il documento, ogni componente combina un **importance score** (quanto quel termine conta nel documento) e un **expansion score** (quanto il documento copre concetti correlati, analogo neurale della query/document expansion). Su MS MARCO Dev, EPIC abbinato a BM25 ottiene $1.3\times$ di speedup rispetto a docTTTTTquery mantenendo il 98% dell'efficacia; contro un Cross-Encoder BERT-large completo, EPIC è $51.5\times$ più veloce (da 3.5s a 68ms) all'83% dell'efficacia originale – un compromesso che rende il modello utilizzabile in produzione dove MonoBERT puro non lo sarebbe.

> **Osservazione: altre tecniche di compressione generiche**
>
> - **Knowledge Distillation**: un modello *teacher* grande (es. BERT full) addestra un modello *student* più piccolo a imitarne le predizioni, mantenendo gran parte delle prestazioni con molti meno parametri.
> - **Quantization**: pesi e attivazioni sono rappresentati con meno bit (es. 8 invece di 32), riducendo memoria e tempo di esecuzione con perdita di accuratezza tipicamente contenuta.
> - **Early Exit**: si inseriscono punti di uscita intermedi nei layer del Transformer; per query o documenti “facili” il modello si ferma ai primi layer, accelerando il tempo medio di inferenza.
>

# Approximate Nearest Neighbor (AkNN) Search

## Similarity Search e Curse of Dimensionality

La **Similarity Search**: dato $q \in \mathbb{R}^d$, trovare il vettore (o i $k$ vettori) in $\mathcal{X}$ che minimizzano una distanza (Euclidea o inverso della Cosine Similarity):

$$
NN(q) = \arg\min_{x\in\mathcal{X}} \text{dist}(q,x)
$$

### Ricerca efficiente in 1D

Se i punti sono pre-ordinati in un array (costo $O(n\log n)$ in indicizzazione), la ricerca del nearest neighbor si riduce a trovare la posizione teorica di $q$ tramite **Binary Search**: il candidato più vicino è necessariamente uno dei due punti adiacenti (predecessore o successore) in quella posizione. Basta confrontare le due distanze. Complessità: $O(\log n)$.

### Ricerca efficiente in 2D

In 2D la tecnica standard è il partizionamento dello spazio tramite **Diagramma di Voronoi**.

> **Definizione: Diagramma di Voronoi**
>
> Partiziona il piano in celle poligonali: ogni cella contiene tutti i punti più vicini a un dato sito (generatore) rispetto a qualsiasi altro. Il numero totale di vertici e archi del diagramma è $\Theta(n)$. Trovare il Top-1 per una query $q$ si riduce a un problema di **2D point location**: individuare la cella di Voronoi a cui $q$ appartiene, risolvibile con le stesse complessità asintotiche del caso 1D ($\Theta(\log n)$ tempo, spazio lineare) – pur essendo, in pratica, un problema implementativo nettamente più complesso.

> **Osservazione: la maledizione della dimensionalità**
>
> Il partizionamento tramite Voronoi non scala con la dimensionalità $d$: la dimensione stessa del diagramma cresce fino a $n^{d/2}$, rendendo la point location esatta in spazi multi-dimensionali impraticabile, con tempo $\Theta((d+\log n)^c)$ e spazio $\Theta(n^d)$. L'alternativa di evitare l'indice e scansionare linearmente tutto il dataset costa $\Theta(nd)$ tempo (spazio lineare) – ma resta comunque insoddisfacente su miliardi di vettori. Entrambe le soluzioni esatte sono quindi impraticabili in alta dimensionalità (es. $d>100$, tipico di word embedding o descrittori visuali): si accetta perciò una piccola perdita di accuratezza (Approximate $k$NN) in cambio di un miglioramento drastico in tempo e memoria, misurando la qualità dell'approssimazione come recall rispetto al groundtruth.

## Famiglie di tecniche AkNN

> **Definizione: ANNOY (alberi)**
>
> Partiziona ricorsivamente lo spazio con iperpiani casuali. Per mitigare i vettori vicini ai confini di partizione, costruisce una **foresta** di alberi: durante la ricerca si attraversano più alberi contemporaneamente aggregando i candidati nelle foglie.

> **Definizione: IVF (clustering)**
>
> Usa un quantizzatore grossolano (K-means) per dividere lo spazio in celle di Voronoi. I vettori sono assegnati al centroide più vicino e memorizzati in liste invertite per centroide. In query, si confronta $q$ solo con i centroidi, si identificano gli $nprobe$ più vicini e si esplorano solo le liste associate, ignorando la maggior parte del dataset.

> **Definizione: LSH (Locality Sensitive Hashing)**
>
> Usa funzioni hash progettate affinché vettori simili abbiano alta probabilità di collidere nello stesso bucket, mentre vettori dissimili abbiano bassa probabilità di collisione. Costruendo molteplici tabelle hash con funzioni randomizzate diverse, aumenta la probabilità di intercettare i vicini corretti.

## Grafi di prossimità e HNSW

I metodi basati su grafi offrono oggi il miglior trade-off velocità/recall. L'idea base è il grafo **NSW** (Navigable Small World): ogni vettore è un nodo collegato ai suoi vicini; grazie alla proprietà “small world” (link a lungo raggio oltre a quelli locali), si naviga il grafo greedily avvicinandosi rapidamente alla destinazione.

> **Definizione: HNSW (Hierarchical Navigable Small World)**
>
> Ispirato dalle **Skip List** di Pugh (livelli multipli di liste collegate per velocizzare la ricerca lineare), organizza i nodi in una gerarchia di livelli:
> - I livelli **superiori** sono sparsi, con collegamenti molto lunghi (grandi salti nello spazio, come “autostrade”).
> - I livelli **inferiori** sono sempre più densi, con collegamenti locali a corto raggio.
>
> La ricerca parte dal livello più alto: ricerca greedy del nodo più vicino a $q$ in quel livello, poi si “scende” al livello sottostante usando quel nodo come punto d'ingresso, raffinando progressivamente fino al livello base (livello 0), dove si ottengono i vicini finali.

![Gerarchia di HNSW: la ricerca parte dall'entry point nel livello più alto (collegamenti a lungo raggio) e scende verso il livello 0, sempre più denso, fino al nearest neighbor.](Files%20and%20Images/fig_hnsw_hierarchy.png)

*Gerarchia di HNSW: la ricerca parte dall'entry point nel livello più alto (collegamenti a lungo raggio) e scende verso il livello 0, sempre più denso, fino al nearest neighbor.*

> **Osservazione: analogia stradale per la gerarchia HNSW**
>
> Per andare da una piccola via nella città A a una piccola via nella città B, non si percorrono solo strade locali: ci si sposta prima su autostrade (livelli superiori del grafo) per coprire rapidamente grandi distanze e avvicinarsi alla destinazione generale; una volta in prossimità della città B, si scende verso strade provinciali e infine cittadine (livelli inferiori) per individuare l'indirizzo esatto. In HNSW questo permette di saltare la maggior parte dei punti non rilevanti nei livelli alti e rifinire la ricerca solo quando si è già nella zona corretta dello spazio, garantendo tempi sub-lineari anche su dataset di scala massiva.

![Analogia con le Skip List di Pugh: più livelli di “collegamenti lunghi” permettono di raggiungere rapidamente la zona del target, poi si scende ai livelli più densi per la ricerca fine.](Files%20and%20Images/fig_hnsw_skiplist.png)

*Analogia con le Skip List di Pugh: più livelli di “collegamenti lunghi” permettono di raggiungere rapidamente la zona del target, poi si scende ai livelli più densi per la ricerca fine.*

> **Osservazione: problemi degli approcci graph-based**
>
> Nonostante l'ottimo trade-off velocità/recall, i grafi soffrono di tre limiti: **alto tempo di costruzione** (per ogni nodo bisogna trovare le connessioni giuste, quindi inserire un vettore è costoso quanto cercarlo, evitando archi ridondanti); **alto uso di spazio** (per ogni vettore si memorizzano molti archi, e per garantire efficienza l'indice deve stare in memoria); **ricerca non cache-friendly** (visitare i vicini di un nodo richiede accessi casuali al forward index).

## Product Quantization (PQ)

Per gestire dataset che non entrano in RAM, o per accelerare il calcolo delle distanze, si usa la **Product Quantization**: lossy, decompone un vettore $x\in\mathbb{R}^D$ ad alta dimensionalità in $m$ sottovettori di dimensione ridotta $D^* = D/m$.

> **Definizione: Procedura PQ**
>
> 1. Il vettore è diviso in $m$ sottovettori $u_1,\dots,u_m$.
> 2. Per ogni sottospazio si esegue clustering (K-means) sui dati di training, creando un **codebook** indipendente con $K$ centroidi (spesso $K=256$, per usare 1 byte per indice).
> 3. Ogni sottovettore è sostituito dall'ID del centroide più vicino nel suo codebook.
> 4. Il vettore compresso è la sequenza di $m$ indici interi, occupando molto meno spazio dei float originali.
>

![Procedura PQ: il vettore originale è diviso in $m$ sottovettori, ciascuno codificato con l'ID del centroide più vicino nel proprio codebook (k-means, $K=256$). La codifica è lossy.](Files%20and%20Images/fig_pq_encoding.png)

*Procedura PQ: il vettore originale è diviso in $m$ sottovettori, ciascuno codificato con l'ID del centroide più vicino nel proprio codebook (k-means, $K=256$). La codifica è lossy.*

> **Definizione: Asymmetric Distance Computation (ADC)**
>
> Durante la ricerca non si ricostruiscono i vettori originali. La query $q$ (non quantizzata, per massima precisione) viene divisa negli stessi $m$ sottovettori; per ciascuno si calcola la distanza verso *tutti* i $K$ centroidi del codebook relativo, salvando i risultati in una **look-up table (LUT)**. La distanza approssimata tra $q$ e un documento codificato è la semplice **somma** dei valori letti dalla tabella agli indici specificati dalla codifica del documento  –  eliminando operazioni in virgola mobile costose sui vettori originali e riducendo l'I/O di memoria.

![ADC: per ciascun sottospazio si calcola la distanza tra il sottovettore di query e tutti i centroidi (look-up table); la distanza finale è la somma dei valori letti agli indici della codifica del documento.](Files%20and%20Images/fig_pq_adc.png)

*ADC: per ciascun sottospazio si calcola la distanza tra il sottovettore di query e tutti i centroidi (look-up table); la distanza finale è la somma dei valori letti agli indici della codifica del documento.*

> **Esempio: PQ su un vettore a 128 dimensioni**
>
> Dividendo un vettore di 128 dimensioni in $m=8$ segmenti da 16 dimensioni ciascuno, la distanza finale tra query e documento è la somma di 8 distanze parziali, ciascuna recuperata istantaneamente dalla look-up table  –  niente calcolo diretto sui vettori originali durante la scansione dell'indice.

## AkNN su vettori sparsi: Seismic

Nel Neural IR con **vettori sparsi appresi** (es. SPLADE) ogni documento è formalmente un insieme di coppie $d=\{(c_1,v_1),(c_2,v_2),\dots\}$, dove $c_i$ è l'indice della componente e $v_i$ il peso neurale. L'obiettivo del kNN è $\arg\max^{(k)}_{v\in D} q^\top v$: trovare i $k$ documenti con prodotto scalare massimo rispetto all'embedding sparso della query. Un kNN “naive” calcolerebbe il dot product per tutti i documenti – troppo costoso su collezioni enormi. Si usa allora un **indice invertito**, che però su vettori appresi mostra due limiti: le stime del dynamic pruning (WAND) diventano poco efficaci perché i pesi non seguono la distribuzione Zipfiana ma sono più “piatti”; e le posting list, molto più lunghe di una query testuale, saturano banda di memoria e CPU in decompressione. L'algoritmo **Seismic** affronta entrambi.

### Seismic: Pruning

Seismic sfrutta la **Concentration of Importance**: dai dati si osserva che circa il 90% del punteggio totale del dot product proviene da circa il 15% dei termini – pochi termini hanno peso grande, gli altri contribuiscono pochissimo. Da qui una potatura su due lati:
- **Pruning lato indice**: per ogni lista invertita di un termine, si ordinano i documenti per peso del termine nel documento e si tengono solo i **top-$\lambda$**; gli altri (peso piccolissimo) vengono rimossi dalla lista.
- **Pruning lato query**: si ordinano le componenti della query per peso decrescente e si scansionano solo le liste dei **top-$\sigma$** termini; le liste dei termini meno importanti non vengono nemmeno lette.

Combinando i due, il numero di prodotti scalari scende da $\sim$4 000 000 (inverted index puro) a $\sim$40 000 – due ordini di grandezza – mantenendo alta l'Accuracy@10. È ovviamente approssimato (qualche vero top-$k$ si può perdere se un documento è rilevante solo grazie a termini deboli, fuori dai top-$\sigma$ o top-$\lambda$), ma la Concentration of Importance garantisce che ciò avvenga raramente.

![Seismic: pruning tramite Inverted Index + Forward Index. La *Concentration of Importance* mostra che poche componenti catturano già il 90% del prodotto scalare, permettendo di scansionare solo i top-$\sigma$ termini di query e i top-$\lambda$ documenti per lista.](Files%20and%20Images/fig_seismic_pruning.png)

*Seismic: pruning tramite Inverted Index + Forward Index. La *Concentration of Importance* mostra che poche componenti catturano già il 90% del prodotto scalare, permettendo di scansionare solo i top-$\sigma$ termini di query e i top-$\lambda$ documenti per lista.*

### Seismic: Blocking and Summaries

Anche dopo il pruning, ogni lista ha ancora migliaia di documenti. Si applica quindi il **Blocking**: si riordinano i documenti della lista e li si raggruppa in **blocchi** di poche decine di documenti *simili tra loro* (nei loro embedding completi), tramite una versione *shallow* di k-means. Se si riesce a stabilire che nessun documento di un blocco può raggiungere la soglia $\tau$ (il minimo per entrare nell'heap dei top-$k$), l'intero blocco viene saltato senza calcolarne i dot product. Per farlo servono le **Summaries**.

> **Definizione: Summary di un blocco**
>
> Per ogni blocco di $B$ documenti con vettori $d^{(1)},\dots,d^{(B)}$, si costruisce un vettore riassuntivo prendendo, per ogni componente $j$, il massimo: $\text{summary}[j]=\max_{b} d^{(b)}[j]$. Questo vettore è un **upper bound** del contributo di qualsiasi documento del blocco. Per renderlo compatto, si ordinano le componenti non nulle per valore decrescente e si tengono solo le $L$ più grandi (summary sparsa).

> **Esempio: costruzione di una summary**
>
> Blocco con $d_1=[3.1,0,0,0.4,0,0]$, $d_2=[0,0.2,0,2.5,0,1.1]$, $d_3=[1.5,0,0,0,0,3.5]$. Il massimo per componente dà $\text{Summary}=[3.1,0.2,0,2.5,0,3.5]$. Con $L=2$ si tengono le due componenti più grandi (3.5 in posizione 5, 3.1 in posizione 0): $\text{Summary}=\{(0,3.1),(5,3.5)\}$. Alla query $q$, si calcola $\widehat{score}_{block} = 3.1\cdot q_0 + 3.5\cdot q_5$; se $\widehat{score}_{block}<\tau$ il blocco viene saltato (0 dot product reali), altrimenti si calcolano i dot product veri per i documenti del blocco.

![Blocking e Summaries: ogni blocco è rappresentato da un vettore sommario (i componenti non-zero più grandi); il blocco viene saltato senza calcolare i dot product se il sommario non supera la soglia $\tau$.](Files%20and%20Images/fig_seismic_blocking.png)

*Blocking e Summaries: ogni blocco è rappresentato da un vettore sommario (i componenti non-zero più grandi); il blocco viene saltato senza calcolare i dot product se il sommario non supera la soglia $\tau$.*

Un **Forward Index** è mantenuto in memoria per la fase di rifinitura: identificati i blocchi promettenti, si recuperano i vettori completi solo per quei documenti e se ne calcola il punteggio esatto. Poiché i documenti in un blocco sono raggruppati per similarità geometrica (non per ID come in un indice classico), se il Summary è poco rilevante è molto probabile che tutto il blocco lo sia: il pruning è quindi più robusto del pruning per ID, e permette latenze nell'ordine dei millisecondi anche con SPLADE in produzione.

> **Esempio: il funnel di Seismic su una collezione da 8.8M documenti**
>
> Su un dataset con $N=8.8$M documenti, vocabolario di 30K componenti e $\sim$150 componenti non-zero per vettore: un **Brute Force** su tutti i documenti richiede 8 841 823 prodotti scalari; il solo **Inverted Index** scende a $\sim$4 000 000; il **Pruning** (top-$\lambda$, top-$\sigma$) arriva a $\sim$40 000; aggiungendo il **Blocking** con i Summary il conteggio crolla a $\sim$5 100 – oltre tre ordini di grandezza in meno del brute force, con Accuracy@10 superiore al 98%.

# Appendice: mappa argomenti d'esame

Questa tabella collega i temi ricorrenti nelle tracce d'esame (Esami A–E) alle sezioni degli appunti dove sono trattati, utile per un ripasso mirato.

| **Argomento d'esame** | **Dove si trova** |
| --- | --- |
| Framework generale LtR, problemi del learning the rank, LambdaMART | Cap. 9 (Framework, osservazione sui problemi) |
| Pointwise / Pairwise / Listwise + gradient descent pointwise | Cap. 9, sez. Pointwise/Pairwise/Listwise |
| Representation-based: dense single/multi-vector, sparso appreso (pro/contro) | Cap. 12, sez. Metodi Representation-based |
| GOSS (definizione + perché riduce training time + esempio) | Cap. 11, sez. GOSS |
| EFB (definizione + perché riduce training time + esempio) | Cap. 11, sez. EFB |
| Word2Vec (CBOW/Skip-gram, risultato scientifico, embedding) | Cap. 12, sez. Word2Vec e FastText |
| Split finding: perché costoso, bottleneck, 3 approcci (histogram, GOSS, EFB) | Cap. 11, sez. Split Finding + osservazione bottleneck |
| kNN in 1D (Binary Search) e 2D (Diagramma di Voronoi) | Cap. 13, sez. Similarity Search |
| HNSW: gerarchia e analogia stradale | Cap. 13, sez. Grafi di prossimità e HNSW |
| Product Quantization + ADC + esempio numerico | Cap. 13, sez. Product Quantization |
| Precision, Recall, DCG, NDCG: definizioni e perché filoni diversi | Cap. 2, sez. Rilevanza Graduata |
| TF-IDF vs BM25: concetti fondamentali e perché BM25 è un'evoluzione | Cap. 5, sez. BM25 (osservazione finale) |
| Simulazione TAAT per query booleana (Pluto AND NOT Pippo) | Cap. 6, Esempio *ex:taat* |
| Elias Gamma/Delta: codifica di un numero, esempio | Cap. 8, Esempio *ex:elias11* e successivo |
| Prefix-free: perché necessario + confronto con Variable-Byte | Cap. 8, osservazioni su codici prefix-free e VByte |
| Toy example 5 documenti: matrici binaria/count/frequency + TF-IDF | Cap. 5, sez. Esempio completo, Esempio *ex:tfidf* |
| Graduated relevance oltre il binario: CG, DCG, NDCG | Cap. 2, sez. Rilevanza Graduata |
| Seismic: struttura dati, limiti dell'inverted index, come li risolve | Cap. 13, sez. *AkNN su vettori sparsi: Seismic* |
| Decision tree: costo split, bottleneck (sorting, cache miss, frammentazione), 3 approcci | Cap. 11, osservazione bottleneck |
| Modelli di linguaggio statistici: unigram, bigram/n-gram, smoothing | Cap. 12, sez. Modelli di Linguaggio Statistici |
| Efficienza Cross-Encoder: PreTTR, EPIC, knowledge distillation/quantization/early exit | Cap. 12, sez. Efficienza dei Cross-Encoder Neurali |
| Efficienza LtR ad alberi: CLEAVER, X-CLEAVER, DART, X-DART, early exit a soglie | Cap. 10, sez. Early Exit ed Efficient Ensemble Trees |
