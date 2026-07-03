# Modelli Linguistici

In questa prima sezione dedicata all'elaborazione del linguaggio naturale, esploreremo le fondamenta dei modelli computazionali usati per la rappresentazione dei testi, partendo dagli approcci stocastici per arrivare alle moderne reti neurali.

### Modelli Probabilistici e Modello Statistico del Linguaggio

L'indagine parte dall'analisi dei **modelli probabilistici**. Un **modello statistico del linguaggio** può essere definito, nella sua essenza, come una distribuzione di probabilità $P$ che viene applicata a specifiche sequenze di termini. Immaginiamo di analizzare un documento $d$ che è composto da una precisa sequenza di parole, come ad esempio $w_{1}w_{2}w_{3}$. Tramite l'applicazione delle leggi della probabilità condizionata, possiamo calcolare la probabilità dell'intero documento tramite la seguente espressione: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2})$. Sulla base delle diverse assunzioni che decidiamo di fare su questa distribuzione di probabilità, abbiamo la possibilità di creare modelli statistici che presentano differenti gradi di complessità. La formula appena citata è, a livello teorico, perfetta: essa non fa alcuna assunzione pregressa ed è quindi capace di modellare in modo esatto le caratteristiche di qualsiasi lingua. Tuttavia, si rivela totalmente impraticabile all'atto pratico, dal momento che per funzionare richiederebbe di imparare le probabilità di letteralmente qualsiasi sequenza di parole che possa esistere nel linguaggio considerato.

### Modello a Unigrammi e Modelli N-gram

Per superare i limiti computazionali del modello generale, occorre introdurre delle assunzioni semplificative. Il **modello a unigrammi** assume che esista un'assoluta indipendenza statistica tra le varie parole di un testo. In base a questa regola, la probabilità che un documento $d$ esista è semplicemente data dal prodotto matematico delle probabilità singole delle sue parole: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2}) = P(w_{1})P(w_{2})P(w_{3})=\prod_{i}P(w_{i})$. Proprio per via di questa forte e irrealistica assunzione di indipendenza, il classificatore bayesiano che sfrutta tale logica viene denominato "naïve" (ingenuo). A livello ingegneristico, questi modelli utilizzano comunemente i logaritmi delle probabilità per poter operare in modo più agevole all'interno di uno spazio lineare: $\log(\prod_{i}P(w_{i}))=\sum_{i}\log(P(w_{i}))$. Bisogna però considerare il problema dei termini rari o mancanti; per ovviare al rischio che il sistema generi probabilità pari a zero annullando il prodotto complessivo, si adotta una tecnica nota come **smoothing**, che solitamente consiste nell'aggiungere artificialmente un'unità ("add one") a tutte le frequenze calcolate.

Un approccio capace di introdurre maggiore ricchezza informativa è il **modello a bigrammi**, o più genericamente il **modello n-gram**. A differenza dell'unigramma, il bigramma assume che vi sia una dipendenza statistica tra una data parola e quella che la precede immediatamente. Di conseguenza, la formula si sviluppa come segue: $P(d)=P(w_{1}w_{2}w_{3})=P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{1}w_{2}) = P(w_{1})P(w_{2}|w_{1})P(w_{3}|w_{2})=\prod_{i}P(w_{i}|w_{i-1})$. Sebbene sembri una modifica banale, questa semplice aggiunta rende il modello già ampiamente in grado di catturare una buona quantità di regolarità intrinseche del linguaggio. Applicando questo concetto su scale più ampie, emerge un evidente compromesso matematico (trade-off): maggiore è la lunghezza dell'n-gram che adottiamo nel nostro modello, maggiore è la quantità di semantica che viene catturata. D'altra parte, però, all'aumentare della lunghezza della stringa si abbassa la significatività statistica del modello stesso, sbilanciandolo verso un comportamento orientato alla pura memorizzazione a scapito della capacità di generalizzazione.

### Modelli Neurali e l'Introduzione di Word2Vec

Il vero punto di rottura rispetto alla tradizione si ottiene con il passaggio ai **modelli neurali**. Lo standard di riferimento in questo ambito è **Word2Vec**. I due principali approcci proposti dall'architettura di Word2Vec, noti come **Skip-gram** e **CBoW** (Continuous Bag-of-Words), definiscono due specifici task speculari: il primo si focalizza sulla predizione di un contesto a partire da una parola nota, mentre il secondo si occupa di predire una parola specifica basandosi sul suo contesto di contorno. Da un punto di vista strutturale, entrambi i meccanismi vengono implementati come una rete neurale lineare dotata di due soli livelli ("two-layers linear neural network"). All'interno di queste reti, sia le parole immesse in input che quelle attese in output sono fornite sotto forma di rappresentazioni di tipo "one-hot". Tali vettori sparsi vengono quindi prima codificati e successivamente decodificati verso e da una **rappresentazione densa** caratterizzata da una dimensionalità molto più contenuta.

![[Pasted image 20260428115609.png]]
### L'Apprendimento degli Embeddings e il Contesto

Risulta fondamentale comprendere che gli **embeddings** (cioè i vettori d'incorporamento) costituiscono, di fatto, un semplice sottoprodotto ("byproduct") del task di predizione delle parole. Anche se le reti affrontano un evidente task di previsione, esse presentano il formidabile vantaggio di poter essere addestrate virtualmente su qualsiasi blocco di testo. Questo processo avviene in modo completamente non supervisionato, rimuovendo la necessità di impiegare lunghi e costosi dati etichettati dall'uomo ("human-labeled data").


Per determinare quali parole facciano parte dell'orizzonte di analisi, si definisce la dimensione di una finestra di contesto ("context window size"). Tale parametro solitamente spazia in un range compreso tra due e cinque parole prima e dopo il termine centrale di riferimento. È interessante notare che adottare finestre di ampiezza maggiore (tipicamente tra i 6 e i 10 lemmi) consente di catturare una più ricca rete semantica, riducendo di conseguenza il peso delle sole relazioni sintattiche locali. Al termine del training, la dimensione tipica dei vettori di embedding risultanti in Word2Vec si assesta usualmente in un valore compreso tra le 200 e le 300 dimensioni.

### Funzionamento Interno dei Livelli di Word2Vec

La struttura della rete Word2Vec si basa sull'interazione di due specifici livelli funzionali. Il primo di questi strati prende in input la matrice $W_I$, che corrisponde alla rappresentazione altamente sparsa delle parole (ovvero il vettore "one-hot" avente una dimensione pari a $|F|$) e ha lo scopo di generare una nuova rappresentazione astratta, denotata come $h$, che incapsula le informazioni del contesto. Successivamente interviene il secondo strato, governato dalla matrice dei pesi $W_o$. Questo livello ha l'incarico di prendere l'astrazione vettoriale $h$ e convertirla in un vettore finale di probabilità $u$, il quale rappresenta l'effettiva predizione del target linguistico ricercato. Infine, tale output viene normalizzato ricorrendo alla nota funzione softmax.

![[Pasted image 20260428120226.png]]

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

![[Pasted image 20260428120316.png]]

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

![[Pasted image 20260428120607.png]]

In un approccio più "moderno" e svincolato, possiamo anche saltare le combinazioni matematiche a priori, e utilizzare direttamente e grezzamente gli embeddings delle singole parole come se fossero il primissimo strato di una più complessa rete neurale profonda, delegando interamente alla capacità esplorativa della rete neurale il gravoso compito di imparare e modellare implicitamente la totalità del contenuto semantico del documento lungo i suoi livelli convoluzionali o ricorrenti.

### Gli Embeddings nell'Ecosistema delle Reti Neurali e il Padding

Approfondendo l'aspetto implementativo all'interno del Deep Learning, il livello deputato all'embedding costituisce, di prassi, il primo strato operativo in assoluto di un'architettura neurale votata all'NLP. Strutturalmente si tratta di un semplice strato lineare fondato su una grande matrice di pesi $W$ la cui dimensione è $|F|\cdot n$, dove il parametro $n$ fissa brutalmente e deterministicamente la dimensione desiderata per lo spazio vettoriale (la lunghezza del vettore embedding per parola). Come accennato, tale matrice si assume l'incarico vitale di tradurre entità linguistiche isolate nelle celebri e lavorabili rappresentazioni dense (dense representations). La natura versatile di questo livello permette di inizializzarne i valori assegnando ai pesi vettori puramente casuali che verranno affinati in seguito, oppure sfruttando le solide basi offerte da vettori preaddestrati. Proprio in riferimento all'addestramento, qualora si scelgano pesi preaddestrati d'alta qualità, lo sviluppatore ha facoltà di "congelarli" mantenendoli fissi per tutta la sessione di addestramento, oppure di sbloccarli e aggiornarli ("updated"), avviando di fatto un processo di adattamento e fine-tuning dell'embedding stesso che gli permetterà di performare al meglio sullo specifico task in elaborazione.

![[Pasted image 20260428120953.png]]
Per analizzare il diagramma esposto in precedenza con un caso d'uso: prendendo un frammento testuale grezzo ("Text") come "all work and no play...", la macchina elabora la sequenza di identificatori, che si configura in una lista intera, ad esempio [2,4,1,8,10,5,0,0,0,0,0,0]. Tale sequenza si infrange sul layer degli Embeddings (guidato dai "Pretrained values" se presenti) scaturendo in uscita un raggruppamento o sequenza di vettori, matrici multidimensionali che racchiudono file come `[0.2, -0.3, 0.9, -0.2... 0.8]`, `[-0.1, 0.7, 0.7, -0.1... -0.1]` e così via. Tutto questo è facilmente toccabile con mano esplorando i branch dei repository GitHub ufficiali di framework come Keras (es. `imdb_cnn.py`), dove viene palesato l'interfacciamento tra livelli linguistici e strati puramente computazionali Convolutional o Recurrent.

Trattare dati in serie comporta però due insidie per l'architettura neurale, per le quali occorrono degli accorgimenti drastici: le OOV words e la necessità imperativa di allineamento dimensionale nel padding. Riguardo al primo problema, poiché i modelli linguistici (LMs) che si basano rigidamente su un vocabolario testuale fisso non sono capaci, per loro stessa natura progettuale, di gestire e modellare parole "Out Of Vocabulary", occorre aggirare l'ostacolo addizionando preventivamente allo spazio un elemento speciale artificiale noto come parola sconosciuta o "unknown word". Ovviamente, per poterla inglobare nell'architettura, la si correda subito del suo embedding isolato da far imparare progressivamente al sistema lungo le epoche di addestramento. Per via del loro funzionamento intrinseco volto alla parallelizzazione computazionale da affidare alle schede grafiche, le NNs tendono a non processare quasi mai frasi singole alla volta, preferendo raggruppare i campioni in un lotto unitario definito "batch" contenente $k$ esempi paralleli. Questo porta al secondo inghippo meccanico: per processare i flussi simultaneamente è strettamente tassativo che le sequenze fornite nel blocco posseggano una lunghezza spaccatamente identica l'una con l'altra. La tecnica per forzare questa uniformità richiede di prendere la frase di calibro maggiore nel lotto e di allungare artificialmente tutte le restanti. Questo avviene inserendo una speciale "**padding** word" (la parola riempitiva neutrale) per allungare le sentenze e fare perno (match) con la lunghezza massima. Anche tale token è affiancato da un suo micro-vettore embedding inattivo e l'inserimento non è arbitrario: la prassi impone d'essere puramente e inflessibilmente coerenti riguardo al posizionamento spaziale del padding, accodandolo o premettendo alle parole sempre e solo da un solo lato (before/after) in ogni computazione per impedire che l'ordine sequenziale causi danni nel processamento.

### L'Evoluzione verso la Self-Attention e i Transformer

Se guardiamo alle ultime frontiere dell'intelligenza artificiale, è chiaro che stiamo vivendo una vera rivoluzione. Negli ultimi anni sono nati modelli linguistici incredibili, capaci di scrivere e comprendere testi in un modo che definire impressionante è poco.

Questa marcia in più non è nata dal nulla, ma da uno sforzo ingegneristico colossale per costruire reti neurali gigantesche. All'inizio abbiamo visto modelli come **ELMo**, che usavano celle **LSTM** per leggere il testo in modo profondo. Poi siamo passati a sistemi nati per il calcolo parallelo estremo, come il famoso **Transformer**, che è la base di quasi tutto quello che usiamo oggi: dai modelli di **OpenAI** a quelli della famiglia **BERT**. A differenza dei vecchi sistemi che capivano solo il significato base delle parole, questi giganti "divorano" intere biblioteche digitali, arrivando a gestire miliardi di parametri.

Il vero segreto di questo successo è la **Self-attention**. Non è un semplice trucchetto, ma una tecnica che permette alla macchina di capire esattamente quanto ogni parola influenzi il significato delle altre all'interno di una specifica frase. In pratica, il sistema non guarda più alla parola in modo isolato, ma accende un faro sul contesto preciso in cui si trova in quel momento.

Se potessimo sbirciare dentro il "cervello" di questi modelli mentre leggono una frase (come ad esempio: _"Trovata a Taiwan. L'apertura alare è di 24-28 mm"_), vedremmo diverse "teste" (**Heads**) lavorare in parallelo, ognuna con un compito specifico:

- C'è la **Head 1-1** che fa una scansione ampia, cercando di capire l'argomento generale del discorso.
    
- C'è la **Head 3-1** che lavora in modo più stretto, cercando di indovinare quale sarà la prossima parola.
    
- Altre teste sono quasi "ossessive" sui dettagli tecnici: la **Head 8-7** si concentra solo sui simboli che separano le frasi (come il tag `[SEP]`), mentre la **Head 11-6** tiene d'occhio solo i punti fermi per capire dove finisce un concetto.
    

Più una linea di connessione tra le parole è scura, più forte è il peso che il modello sta dando a quel legame. Questo modo di smontare il testo a strati ricorda molto come funzionano le **CNN** (le reti usate per le immagini): lì i primi filtri riconoscono solo linee e contorni semplici, mentre i livelli successivi mettono insieme i pezzi per riconoscere oggetti complessi. Qui succede lo stesso: si parte dai dettagli grammaticali per arrivare a comprendere concetti astratti e profondi.

![[Pasted image 20260428121508.png]]

### Rappresentazioni Dense e Operazioni di Information Retrieval

Un’applicazione davvero potente, dove i vettori e l'attenzione mostrano i loro frutti migliori, è il recupero veloce di informazioni da database giganteschi attraverso le cosiddette **Reti Neurali Siamese**.

In questo sistema il lavoro viene sdoppiato in due flussi diversi ma speculari:

- **Il lavoro "dietro le quinte" (Offline):** Tutto inizia con l'elaborazione dei documenti. Ogni documento viene dato in pasto a una rete neurale che ne mastica i contenuti e li trasforma in un **Feature Vector**, ovvero una stringa di numeri che riassume il significato del testo. Questo pacchetto compatto viene salvato nel database.
    
- **Il lavoro "in diretta" (Runtime):** Quando tu scrivi una domanda (**query**), il sistema le fa fare lo stesso identico percorso. Anche la tua breve domanda diventa un vettore numerico istantaneo.
    

A questo punto, il gioco è fatto: il computer mette "faccia a faccia" il vettore della tua domanda con quelli dei documenti usando un calcolo matematico chiamato **prodotto scalare (dot product)**. Il risultato è il punteggio di rilevanza $S_{q,d}$, che ci dice quanto la tua domanda e quel documento sono vicini a livello di significato.

Tutta questa strategia si basa sulla ricerca dei "vicini più prossimi", tecnica nota come **k-Nearest Neighbour (KNN)**. L'obiettivo è trovare in modo spietato i punti che, in un immenso spazio geometrico a tante dimensioni, sono più vicini al punto della tua domanda.

Tuttavia, c'è un problema di prestazioni: confrontare la tua domanda con _ogni singolo_ documento del mondo (la ricerca **Esatta**) è lentissimo e costoso. Matematicamente, questo sforzo si misura con la notazione $O(n \log k)$ e richiede l'uso di strutture dati pesanti per tenere in memoria solo i risultati migliori.

Per evitare questo blocco, chi gestisce i Big Data sceglie quasi sempre la via della velocità, accettando un piccolo margine d'errore. Si parla di **ANN (Approximate Nearest Neighbour)**. Invece di controllare tutto, si usano dei "trucchi" per trovare i risultati in modo approssimativo ma fulmineo:

- **Alberi (Trees):** Dividono lo spazio in zone sempre più piccole per scartare subito aree intere.
    
- **Grafi (Graphs):** Creano dei sentieri tra i punti vicini per saltare rapidamente da un concetto all'altro.
    
- **Hash (LSH):** Usano delle funzioni per "etichettare" i documenti simili e metterli nello stesso cassetto.
    
- **Quantizzazione:** Comprimono i numeri dei vettori per renderli ancora più leggeri da calcolare.
    

In breve, si preferisce essere "quasi precisi" in un millisecondo piuttosto che "perfetti" dopo aver aspettato un minuto.

![[Pasted image 20260428121703.png]]
### Conclusioni Fondamentali sui Modelli Linguistici

Per concludere questo viaggio tra teoria e pratica, dobbiamo ammettere che i **Language Models** sono oggi gli strumenti più potenti e flessibili che abbiamo per gestire le informazioni. La cosa incredibile è che riescono a imparare tutto da soli, in modo **unsupervised** (senza che un umano debba spiegargli nulla), partendo da qualsiasi testo complesso per trasformarlo in una rappresentazione che il computer può "leggere" e capire perfettamente.

Questa trasformazione dei testi in vettori numerici offre ai data scientist due vantaggi enormi:

- **Esplorazione del dominio:** Questi modelli permettono di mappare velocemente un settore specifico o una lingua difficile. Riescono a trovare in automatico i termini chiave e, soprattutto, a svelare i legami nascosti e le relazioni tra le parole che a noi sfuggirebbero. In pratica, rendono comprensibile un groviglio di dati che prima sembrava indecifrabile.
    
- **Saggezza condivisa (Transfer Learning):** I modelli funzionano come una spugna che assorbe tutta la conoscenza umana disponibile sul web. Questo significa che se devi creare un nuovo piccolo programma per un compito specifico, non devi partire da zero. Puoi "iniettare" in una piccola rete neurale la conoscenza già accumulata dai grandi modelli.
    

Grazie a questo travaso di intelligenza, anche un modello minuscolo diventa subito molto capace, migliorando la sua **generalization ability** (la capacità di cavarsela in situazioni nuove) e raggiungendo risultati straordinari in pochissimo tempo. In poche parole, i Language Models non sono solo archivi di dati, ma veri e propri motori di ragionamento che rendono ogni altra applicazione software più intelligente e veloce.

---

### Concetti Chiave

- **FastText e OOV**: Evoluzione algoritmica applicata alla base dei sistemi di word embedding di concezione classica per esplorare le parole a livello sub-strutturale (n-grammi). Permette al sistema computazionale di superare il limite della parola ignota fuori dal dizionario addestrato (Out Of Vocabulary).

- **Doc2Vec**: Estensione formale per reti neurali sviluppata al fine di espandere le proprietà architetturali dell'algoritmo Word2Vec introducendo in input dei segnali identificativi o tag per mappare il senso logico testuale di un unico documento monolitico intero nei medesimi e collaudati spazi di vettorializzazione creati per accogliere singole parole discrete.

- **Self-attention**: L'ingegnosa tattica focale asincrona impiegata da Reti Transformer rivoluzionarie come BERT tramite blocchi denominati "Head". Il suo scopo principe è discernere e pesare indipendentemente ed ecletticamente il livello matematico e semantico delle influenze del micro e del macro contesto periferico frasale in cui giace avvolta una determinata parola osservata, prestando attenzioni mirate in direzione passata, futura o sui delimitatori frasali artificiali.

- **Approssimazione KNN (ANN)**: Assieme matematico di metodologie computazionali e architetturali in ambito Information Retrieval e vettoriale il cui intento unico è arginare le esplosioni o impennate nei rallentamenti algoritmici classici della tecnica Nearest Neighbour Esatta ($O(n \log k)$), operando calcoli con tolleranza di precisione sfruttando aggregati ad alberi informatici, cluster a grafo o hash geometrici di quantizzazione direzionale (LSH).

---
