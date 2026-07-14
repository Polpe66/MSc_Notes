### Machine Learning per l'IR e Computazione Online

L'architettura tipica di un sistema di Learning to Rank che opera in una fase di online computation prevede l'estrazione di un **vettore di feature** elaborato manualmente. In questo processo, un documento $d$ composto da $m$ token e una query $q$ formata da $n$ token vengono passati a un estrattore di feature (Feature Extractor). L'input per l'addestramento è dunque costituito da questo vettore di feature, che modella la rilevanza della coppia query-documento, unito alle rispettive etichette (labels). Queste informazioni vengono poi fornite a un **Modello di Ranking**, il quale si occupa di calcolare un punteggio finale di rilevanza, indicato tipicamente come $S_{q,d}$. L'approccio che ad oggi rappresenta lo stato dell'arte si basa su una foresta composta da migliaia di alberi di regressione. Sebbene questa soluzione garantisca una qualità altissima dei risultati, il suo utilizzo in produzione risulta computazionalmente molto dispendioso.

![[Pasted image 20260417122504.png]]

### La Complessità del Ranking e la Proxy Loss

Il task di addestrare un modello di ranking si rivela un'operazione estremamente complessa. L'obiettivo principale è imparare l'ordinamento (il ranking) stesso e non la singola etichetta del documento, lavorando su dataset enormi caratterizzati da centinaia di feature differenti. Il problema matematico fondamentale è che le tradizionali misure di qualità del ranking basate sul grado di posizionamento, come NDCG, ERR e MAP, dipendono intrinsecamente dall'ordine in cui i documenti sono stati ordinati. Di conseguenza, queste funzioni non presentano derivate agevoli e impediscono l'applicazione diretta di algoritmi classici come la discesa del gradiente (gradient descent). 

Osservando i documenti (es. $d_0$, $d_1$, $d_2$, $d_3$), il gradiente di queste metriche rispetto ai parametri del modello risulta essere o pari a 0, se le variazioni dei pesi non hanno alterato l'ordinamento, oppure indefinito a causa delle repentine discontinuità della funzione. La soluzione adottata nell'IR moderno è l'introduzione di una **Proxy Loss function**. Questa funzione deve essere differenziabile e, allo stesso tempo, mostrare un comportamento il più possibile fedele alla funzione di costo originale, permettendo così al modello di apprendere con successo i parametri ottimali.

![[Pasted image 20260417122652.png]]
### Algoritmi Point-Wise e Alberi di Decisione

Il primo gruppo di algoritmi analizzabili è quello **Point-Wise**. In questa famiglia di modelli, ogni documento viene valutato in modo strettamente indipendente dagli altri. Durante la fase di addestramento, per una singola istanza formata dal documento $d_i$ e dalla sua etichetta $y_i$, non viene utilizzata alcuna informazione relativa agli altri candidati associati alla medesima query. L'algoritmo ottimizza pertanto una funzione di costo differente rispetto alle metriche list-wise e lo fa sfruttando vari approcci, tra cui la Regressione, la Classificazione Multi-Classe o la Regressione Ordinale.

All'interno degli algoritmi basati sulla regressione, assumono grande importanza i **Gradient Boosting Regression Trees (GBRT)**. In questo specifico modello di addestramento, la Loss Function impiegata è orientata alla minimizzazione della Somma degli Errori Quadratici (SSE). 

Prima di approfondire i GBRT, è essenziale comprendere la struttura di un Albero di Decisione e, di conseguenza, di un Albero di Regressione. In un tipico albero di decisione, usato ad esempio per la classificazione animale, lo spazio viene diviso tramite domande binarie (l'animale è più grande o più piccolo di 1 metro?, ha le corna?, ha due gambe?, le corna sono più lunghe di 10 cm?, indossa un collare?, ha le ali?, ha la coda?) fino a giungere a una classificazione precisa. Analogamente, un albero di regressione esegue un partizionamento dello spazio dei predittori. Basandosi su variabili, ad esempio $x1$ e $x2$, applica soglie di split sequenziali (come $x2 < 3.1$ o $x1 \geq 6.6$) per instradare i campioni verso le foglie. A differenza della classificazione, ogni nodo foglia restituisce un valore continuo $y$ (come $y=0.75$, $y=2.2$, fino a $y=6.3$), che rappresenta la predizione del punteggio.

![[Pasted image 20260417122739.png]]

### Gradient Boosting Regression Trees (GBRT)

I GBRT si fondano sul concetto di foreste di alberi, includendo anche algoritmi come Lambda MART, Random Forest e Oblivious Trees. Queste architetture costituiscono un "ensemble" di *weak learners*, dove ciascun albero contribuisce fornendo un punteggio parziale. Al momento dello scoring vero e proprio, un vantaggio cruciale è che tutti gli alberi ($T_1, T_2, \dots, T_n$) possono processare la coppia (q, d) ed essere valutati in modo indipendente, producendo dei punteggi parziali ($S_1, S_2, \dots, S_n$). Il punteggio documentale finale viene poi calcolato aggregando tali contributi parziali mediante la formula: $s(d) = \sum_{i=1}^n w_i s_i$.

L'addestramento dei GBRT avviene tramite un algoritmo strettamente iterativo in cui il modello finale è espresso come $F(d) = \sum_i f_i(d)$. Ciascun termine $f_i$ rappresenta uno dei *weak learners* ed è concepito matematicamente come un passo nella direzione ottimale di minimizzazione dell'errore, comportandosi come uno step di massima pendenza (steepest descent) calcolato tramite line-search. In formule, questo passo si traduce nella discesa lungo il gradiente negativo: $f_i(d) = -\rho_i g_i(d)$. 

Data una funzione di perdita $L = SSE/2$ , il gradiente $-g_i(d)$ viene formalizzato tramite la derivata $-[\frac{\partial L(y,f(d))}{\partial f(d)}]_{f=\sum_{j<i}f_j}$. Calcolando esplicitamente la derivata, $-\frac{\partial[\frac{1}{2}SSE(y,f(d))]}{\partial f(d)} = -\frac{\partial[\frac{1}{2}\sum(y-f(d))^2]}{\partial f(d)}$, si ottiene la pseudo-risposta $y - f(d)$, che rappresenta banalmente il residuo, ovvero l'errore commesso dalle predizioni accumulate finora rispetto all'etichetta reale. L'algoritmo procede approssimando questo gradiente $g_i$ per mezzo di un nuovo albero di regressione $t_i$. Geometricamente, ciò significa che ogni nuovo albero (da $t_1$ a $t_3$) colma iterativamente la distanza tra il punteggio predetto $F(d)$ e l'obiettivo $y$.

![[Pasted image 20260417123012.png]]



### Algoritmi Pair-Wise e il Modello RankNet

Passando al secondo approccio principale, negli algoritmi **Pair-wise** l'unità fondamentale di analisi cessa di essere il singolo documento e diventa la coppia. 
**RankNet** rappresenta una delle soluzioni Pair-wise più affermate e opera stimando la probabilità che, all'interno di un'istanza formativa, un documento $d_i$ sia superiore in rilevanza rispetto a un documento $d_j$. Definita la differenza dei punteggi predetta dal modello per i due documenti come $o_{ij} = F(d_i) - F(d_j)$ , questa probabilità viene calcolata tramite la funzione logistica $P_{ij} = \frac{e^{o_{ij}}}{1 + e^{o_{ij}}}$. 
Assumendo che $T_{ij}$ indichi la probabilità reale derivata dalle etichette, RankNet sfrutta una funzione di Cross Entropy Loss definita come $C_{ij} = -T_{ij} \log P_{ij} - (1-T_{ij}) \log(1-P_{ij})$. 

L'algoritmo filtra il dataset analizzando solamente le coppie dove sussiste una chiara preferenza, ovvero dove l'etichetta di $d_i$ è maggiore di quella di $d_j$ ($y_i > y_j$). In questo scenario, la loss si semplifica notevolmente diventando $C_{ij} = \log(1 + e^{-o_{ij}})$. Da questa equazione si dediuce il comportamento del modello: se i documenti sono correttamente ordinati, con una predizione marcatamente forte ($o_{ij} \to +\infty$), il costo di errore tende naturalmente a zero ($C_{ij} \to 0$). 

Al contrario, se la rete si sbaglia e inverte l'ordine corretto restituendo una forte discrepanza negativa ($o_{ij} \to -\infty$), il costo esplode verso infinito ($C_{ij} \to +\infty$). Poiché questa loss function si mantiene strettamente differenziabile, viene sfruttata efficacemente per addestrare Reti Neurali Artificiali (ANN) per mezzo del noto algoritmo di back-propagation.

Altri approcci inclusi nella famiglia Pair-wise comprendono algoritmi come Ranking-SVM e RankBoost. I dati sperimentali confermano che RankNet ottiene performance generali migliori rispetto a questi due competitor per quanto riguarda l'accuratezza del ranking. Esiste, tuttavia, un limite architetturale rilevante da tenere in considerazione: l'andamento della funzione di costo in RankNet non risulta essere adeguatamente correlato con i miglioramenti qualitativi reali che si possono misurare tramite NDCG@5 (o altre metriche orientate all'ordine puro) all'aumentare delle epoche di addestramento.

![[Pasted image 20260423120842.png]]
### Algoritmi List-wise e LambdaMART

Evoluzionando le filosofie precedenti, gli algoritmi **List-wise**, di cui **LambdaMART** è il massimo esponente, ottimizzano in modo diretto le metriche di valutazione focalizzandosi sull'intera lista dei documenti. Ricordando che i Gradient Boosting Regression Trees (GBRT) hanno la necessità intrinseca di calcolare un gradiente $g_i$ per ciascun documento $d_i$ sottoposto ad analisi , LambdaMART comincia col calcolare la stima di questo gradiente paragonando il documento in esame con un diverso $d_j$, ponendo come condizione essenziale che l'etichetta di rilevanza $y_i$ sia superiore a $y_j$. Questo scarto è descritto dal coefficiente $\lambda_{ij}$, che viene modellato come il prodotto algebrico tra la derivata del costo negativo ricavata da RankNet e il differenziale di qualità (ossia il valore assoluto della variazione $|\Delta NDCG|$) ottenuto nel simulare un ipotetico scambio di posizione tra $d_i$ e $d_j$. La formula completa si esprime quindi come:
![[Pasted image 20260423121114.png]]

L'analisi di una specifica istanza di training, definita dalla query e dai rispettivi documenti ($d_2, d_3, \dots, d_{|q|}$), rivela un comportamento elegante. Se i due documenti sono già ordinati correttamente in partenza (condizione in cui $o_{ij} \to +\infty$), il gradiente sfuma verso lo zero ($\lambda_{ij} \to 0$). 
Viceversa, in caso di cattivo posizionamento ($o_{ij} \to -\infty$), il gradiente si innalza fino a raggiungere il pieno valore dell'errore metrico di ranking $|\Delta NDCG|$. Attraverso questo semplice meccanismo matematico, si obbliga la rete a dare un'importanza molto superiore ai documenti che si posizionano in alto (i top documents), in quanto notoriamente più rilevanti per la soddisfazione dell'utente finale. Al termine di questi step, l'algoritmo GBRT con Lambda Gradients calcola la stima del gradiente finale $g_i$ confrontando $d_i$ con tutti gli altri candidati associati alla query; il calcolo avviene addizionando i pesi delle coppie correttamente disposte e sottraendo invece i valori associati a quelle fallite, applicando la semplice espressione $g_i = \lambda_i = \sum_{y_i>y_j} \lambda_{ij} - \sum_{y_i<y_j} \lambda_{ij}$


![[Pasted image 20260423121230.png]]

### Risultati Sperimentali ed Evoluzione dei Modelli List-wise

Per valutare la bontà pratica di questi approcci, si osservano tipicamente le prestazioni espresse in termini di metrica NDCG@10 eseguite su vari set di dati (Dataset LtR) aperti al pubblico. Come si evince confrontando la tabella sottostante, il passaggio da approcci classici come RankingSVM a GBRT e LambdaMART ha prodotto valori fortemente eterogenei a seconda del dataset interrogato (quali MSN10K, Y!S1, Y!S2 e Istella-S). Il mercato accademico e industriale non si è comunque fermato, elaborando ulteriori alternative competitive che includono ListNet, ListMLE, Approximate Rank, SVM AP e RankGP.

![[Pasted image 20260423121558.png]]

Esaminando storicamente lo sviluppo del Machine Learning in ambito di ricerca testuale, emerge una timeline ricca, nata nei periodi antecedenti al 2005 e proseguita verso approcci concorrenti Pointwise, Pairwise (come RankBoost e RankNet) e Listwise (tra cui AdaRank e ListNet). Gli approcci moderni per massimizzare le misure IR includono oggi varianti come DirectRank, Lambda Mart, BLMart, SSLambdaMART, CoList, LogisticRank e Lambda Loss. Simultaneamente, il Deep Learning sta innovando pesantemente il *matching* tra documento e query, proponendo architetture Conv.DNN, DSSM, meccanismi basati sul Dual-Embedding, nonché tecniche legate alla Weak Supervision e al Neural Click Model. Da citare infine lo spostamento d'attenzione verso sistemi di On-line learning interattivo, regolati da approcci probabilistici come Multi-armed bandits, Dueling bandits e K-armed dueling bandits.

### L'Egemonia Pratica dei GBRT e le Tecniche di Distillazione

L'adozione quasi universale dei GBRT nel panorama industriale deriva da una lunga striscia di successi ottenuti nei più disparati Data Challenge su scala mondiale. L'evidenza principale risale al famoso Yahoo! LtR Challenge, vinto da un team la cui architettura mescolava 12 differenti modelli di ranking; di questi, 8 erano proprio costituiti da modelli Lambda-MART istanziati con grandezze fino a ben 3.000 alberi decisionali l'uno. Questa forte predominanza è stata riconfermata in uno studio statistico del 2015 sulle competizioni Kaggle, indicando che la stragrande maggioranza delle soluzioni trionfanti si appoggiava pesantemente ai GBRT a discapito delle reti deep learning, e che le top-10 squadre arrivate alla KDDCup dello stesso anno utilizzassero tutte algoritmi della famiglia GBRT. Di conseguenza, giganti tecnologici e team indipendenti hanno pubblicato formidabili e ottimizzate piattaforme open-source: XGBoost, la soluzione LightGBM presentata da Microsoft e CatBoost promossa da Yandex. Tali soluzioni sono divenute lo standard in quanto pluggable, potendo essere collegate in modo nativo su sistemi leader come Apache Lucene e Solr.

Eppure, persiste un dubbio architetturale: se le Reti Neurali Profonde (DNN) tendono ad essere intrinsecamente più veloci nell'eseguire passaggi in inferenza rispetto alle lunghe foreste di regressione, com'è possibile combinare le velocità delle prime con l'affidabilità delle seconde?. La soluzione proposta dalla letteratura è una metodologia definita **Ranking Distillation**. Il principio razionale si basa sull'accettazione fiduciaria dell'accuratezza altissima di LambdaMART, allo scopo però di addestrare una DNN che ne imiti semplicemente l'output a *run time*. In sintesi, un'architettura a rete neurale artificiale (ANN) viene costretta ad approssimare pedissequamente gli output prodotti in partenza da un algoritmo LambdaMART, ignorando totalmente i *training labels* originali utilizzati in precedenza. Ai fini addestrativi, lo spazio campionario del dataset subisce un arricchimento focalizzato attorno a zone di chiara discontinuità matematica, ovvero gli *split points* degli alberi da imitare. Sono stati eseguiti test su modelli neurali *Fully Connected*, in particolare varianti a 4 strati (con dimensioni $2000 \times 500 \times 500 \times 100$) e versioni più leggere a soli 2 strati ($500 \times 100$). I test in termini di metrica di precisione media MAP dimostrano i seguenti valori:



### Il Collo di Bottiglia nel Single-Stage Ranking

Sebbene i modelli matematici siano complessi, la vera sfida pratica sopraggiunge al momento del calcolo. Immaginare un ecosistema a stadio singolo, definito **Single-Stage Ranking**, significa concettualizzare un passaggio unico e diretto: una richiesta utente (Query + Matching Docs) che viene somministrata istantaneamente al Ranker intero, il quale produce a stretto giro l'ordine dei risultati definitivi. Questa logica richiede che l'algoritmo addestrato venga calcolato rigorosamente per ciascun documento nel sistema, e allo stesso tempo obbliga a estrarre massivamente tutti i vettori e le relative feature associate. Un simile approccio su scala produttiva risulta del tutto irrealizzabile per ragioni di efficienza e latenza.

![[Pasted image 20260423122232.png]]

Questo vicolo cieco introduce le tre maggiori compromissioni o trade-off tra efficienza computazionale ed efficacia applicativa (efficacy) nel Learning to Rank. Il primissimo attrito è dato dal **Feature Computation Trade-off**, che costringe i tecnici a valutare accuratamente un delicato bilanciamento di costo tra impiegare *feature* lente, onerose ed eccezionalmente discriminanti in opposizione all'adozione esclusiva di estrazioni computazionalmente molto blande ma con scarso o nullo potere di differenziazione informativa.

### Il Design del Multi-Stage Ranking

Per superare i limiti di velocità quando si gestiscono troppi dati, nel settore si usa un'architettura chiamata **Multi-Stage Ranking**. Il cuore di tutto è la **Pipelined Ranking Architecture**, un sistema a cascata pensato per estrarre i risultati migliori, i cosiddetti **top-k**, senza far saltare i server. Spesso questo si traduce in un modello a due fasi chiamato **Two-Stage Ranking**.

Si parte dallo **Stage 1**, dove un ranker molto semplice usa degli indici invertiti per fare una scansione rapidissima. In questa fase l'obiettivo è il **Recall**, ovvero recuperare quanti più documenti pertinenti possibile con il minor sforzo computazionale. Il sistema screma subito il grosso del web e tira fuori una lista di "N candidati", un numero che è comunque molto più grande del risultato finale che serve a noi ($N \gg k$).

Solo su questa piccola fetta di dati entra in gioco lo **Stage 2**. Qui interviene un **Top Ranker** molto più sofisticato, basato su algoritmi di apprendimento automatico, che punta tutto sulla **Precision**. Dato che deve lavorare su pochi documenti e non su miliardi, può permettersi di analizzarli a fondo per decidere l'ordine esatto e mostrare all'utente solo i risultati migliori. In pratica, questo trucco serve a risparmiare energia e potenza di calcolo, usandole solo dove serve davvero invece di sprecarle su pagine inutili.

![[Pasted image 20260423145736.png]]

A questo punto però sorge un problema logico: come si decide quanto deve essere grande questa fetta intermedia di candidati? È qui che i progettisti devono affrontare il cosiddetto **Number of Matching Candidates Trade-off**, ovvero una sorta di compromesso strategico.

In pratica, se tieni una "vasca" di candidati troppo grande, avrai sicuramente dei risultati di altissima qualità perché è difficile che qualcosa di buono ti sfugga, ma i costi di gestione e la potenza di calcolo necessaria saliranno alle stelle. Al contrario, se fai una scrematura troppo aggressiva e tieni pochi documenti, il sistema sarà velocissimo e leggero, ma rischi di produrre risultati deludenti perché potresti aver scartato per errore proprio le pagine migliori.

Per darti un'idea delle misure usate nel settore, ci sono dataset standard come _Gov2_ che filtrano a 1.000 documenti, mentre per analisi come _ClueWeb09-B_ di solito si oscilla tra i 1.000 e i 2.000 candidati. Quando poi passiamo ai colossi del web o alle grandi infrastrutture aziendali, le cose si fanno ancora più serie: lì i bacini operativi arrivano a contenere centinaia di migliaia di candidati. Per gestire una mole simile, il lavoro viene diviso orizzontalmente e processato in parallelo da intere flotte composte da centinaia di macchine che lavorano contemporaneamente.

Oltre al sistema a due passaggi, oggi si usano spesso pipeline di **Multi-Stage Ranking a 3 stadi**. In questo caso si aggiunge un terzo livello dedicato al **Contextual Ranking**, che lavora solo su una cerchia ristrettissima di circa 30 risultati. L'idea è quella di guardare al "contesto": il sistema non analizza più i documenti da soli, ma li confronta tra loro usando statistiche trasversali, come medie o varianze dei dati, e modelli che estraggono i temi dominanti per capire quanto i risultati siano coerenti tra loro.

Per evitare che l'utente debba aspettare troppo tempo davanti allo schermo, il primo e il secondo stadio devono essere gestiti in modo fulmineo direttamente sul nodo di interazione, ovvero il server che risponde alla ricerca. Progettare un sistema che scali bene però non è facile: bisogna decidere quali modelli usare, quante caratteristiche tecniche (feature) analizzare in ogni fase e quanti documenti far passare da un imbuto all'altro.

Analizzando circa 200 diverse configurazioni, si è visto che le prestazioni migliori si ottengono con pipeline a 3 stadi che stringono il campo in modo graduale. Ad esempio, si può partire da 2.500 candidati nel primo passaggio per poi scendere a 700 in quello intermedio. Queste dinamiche sono diventate così complesse che oggi non si decide più a occhio quanto debba essere grande il numero di candidati o come strutturare la pipeline: si usano dei modelli predittivi già durante la fase di addestramento (training time) per calcolare in anticipo quale sia il bilanciamento perfetto tra velocità e precisione.

In sintesi, un'architettura completa deve far girare bene tutti questi ingranaggi bilanciando l'ultimo grande compromesso: il **Model Complexity Trade-off**. L'idea è che non serve usare un supercomputer per scartare i risultati palesemente inutili; meglio dosare la potenza in base alla difficoltà del compito.

In una pipeline intelligente come il **Multi-Stage Ranking**, si parte usando classificatori semplicissimi e velocissimi per il recupero grezzo. In questa fase si usano strumenti agili come _Coordinate Ascent_, _Ridge Regression_ o versioni leggere di _SVM-Rank_. Qui non cerchiamo la perfezione, ma la rapidità pura per gestire la massa iniziale di dati.

Salendo verso la cima dell'imbuto, si passa a modelli via via più densi e complessi. Se lo **Stage i-1** è il cosiddetto **Cheap Ranker** (quello economico e veloce), lo **Stage i** diventa l'**Accurate Ranker**, dove si iniziano a usare modelli più strutturati come _ListNet_ o _Oblivious Lambda-Mart_.

Solo quando arriviamo alla fine della selezione, allo **Stage i+1**, entra in campo il **Very Accurate Ranker**. Qui si usano i colossi dell'analisi, come le foreste di alberi decisionali (_Random Forest_) o i potenti modelli _GBRT_ (Gradient Boosted Regression Trees). Questi "cervelloni" pesanti si occupano della rifinitura suprema: analizzano chirurgicamente solo i pochissimi elementi rimasti per decidere l'ordine perfetto da mostrare all'utente. In questo modo, la complessità del calcolo aumenta solo quando il numero di documenti diminuisce, rendendo tutto il sistema fluido ed efficace.

![[Pasted image 20260423150058.png]]
