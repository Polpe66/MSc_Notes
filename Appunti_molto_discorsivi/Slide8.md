# Slide 8: Appunti di Information Retrieval: Trade-off tra Efficienza ed Efficacia

Questa sezione esplora il delicato equilibrio tra l'accuratezza dei risultati di ricerca e i costi computazionali associati, concentrandosi in particolare sull'ottimizzazione dei modelli di apprendimento basati su alberi decisionali.

### Il Calcolo Online della Rilevanza

Il processo base per calcolare la rilevanza di un documento per una data query si articola in una serie di passaggi sequenziali. Inizialmente, un estrattore di feature elabora i token del documento (di lunghezza $m$) e della query (di lunghezza $n$) per produrre un vettore di feature estratte manualmente. Questo vettore, che modella la rilevanza della specifica coppia query-documento, viene poi fornito in input al modello di ranking (Ranking Model) insieme alle relative etichette (labels), il quale infine emette un punteggio di rilevanza finale $s_{q,d}$. Attualmente, l'approccio *state-of-the-art* si basa su foreste composte da migliaia di alberi di regressione: sebbene garantiscano un'alta qualità dei risultati, il loro impiego risulta computazionalmente molto oneroso.

![[Pasted image 20260423150433.png]]

### Efficienza nel Machine Learning per l'IR

I modelli basati sul *machine learning* pensati per un ranking orientato alla precisione si fondano prevalentemente su ensemble di alberi, noti come **Tree forests** (ad esempio GBRT, LambdaMART, Random Forest e Oblivious Trees). Questi sistemi operano unendo l'output di un insieme di *weak learners*, dove ciascun albero contribuisce al punteggio totale con un punteggio parziale. La formula che definisce il punteggio complessivo $s(d)$ di un documento è data dalla somma dei punteggi parziali di ogni albero $T_i$: $s(d)=\sum_{i=1}^{n}s_{i}$. Il costo computazionale deriva dal fatto che, al momento dello *scoring*, ogni albero deve essere elaborato in modo del tutto indipendente. Per dare un'idea dell'onere, supponendo di avere 1.000 documenti per singola query e un modello con 3.000 alberi con una profondità di 10 nodi, il sistema deve eseguire 30.000 test per ogni documento ($3.000 \times 10$), traducendosi nell'impressionante cifra di 30 milioni di test per singola query.
![[Pasted image 20260423151126.png]]

### Anatomia di un Albero di Decisione

Strutturalmente, questi modelli ad albero valutano la rilevanza attraverso due componenti principali. I nodi interni rappresentano delle condizioni booleane specifiche sulle feature $f$, applicando una certa soglia (threshold). Le foglie (nodi terminali), invece, contengono la vera e propria predizione del valore di rilevanza. Come anticipato, il punteggio finale di rilevanza assegnato a una coppia query-documento equivale alla somma di tutte le singole predizioni generate dagli alberi del modello. I pesi e i tagli di questi nodi (come si evince dall'addestramento, ad esempio su feature come $F_1, F_2, F_3, F_4, F_6, F_8$) determinano come gli alberi vengono adattati (fitted) ai dati.
![[Pasted image 20260423151402.png]]

### Architettura a Cascata dei Motori di Ricerca

Per gestire l'immensa mole di dati, i moderni motori di ricerca suddividono il lavoro in due stadi principali mediante un'architettura a cascata. Il **Primo Stadio (Stage 1)** si basa su un approccio orientato alla *recall* (Recall-oriented Ranking). Ricevendo la query dell'utente, un Base Ranker accede a un Inverted Index per recuperare rapidamente un ampio set iniziale di $N$ documenti pertinenti (Matching Docs). Successivamente, il **Secondo Stadio (Stage 2)** prende in carico i top-N documenti e applica un approccio più sofisticato orientato alla *precisione* (Precision-oriented Ranking). In questa fase interviene il Top Ranker, che sfruttando funzionalità avanzate (Features) e algoritmi di Learning to Rank (LtR), seleziona un sottoinsieme estremamente ristretto di $K$ documenti finali, costituendo la pagina dei risultati mostrata all'utente.
![[Pasted image 20260423151427.png]]
![[Pasted image 20260423151506.png]]

### Linee di Ricerca sul Trade-off Efficienza/Efficacia

L'efficienza nei sistemi LtR viene affrontata secondo tre principali filoni di ricerca. Il primo riguarda l'ottimizzazione dell'efficienza direttamente all'interno del processo di apprendimento (*learning process*). Il secondo esplora il calcolo approssimato dei punteggi e l'utilizzo di strutture a cascata efficienti. Infine, il terzo si focalizza sull'attraversamento veloce e ottimizzato dei modelli tree-based già costruiti. Ognuno di questi approcci genera impatti significativamente diversi sull'architettura complessiva di ranking, influenzando sia l'addestramento (Training Data e Learning to Rank Technique) sia l'applicazione finale del modello sui campioni (K docs).

### Ottimizzare l'Efficienza durante l'Apprendimento: Il Modello MEET

Nel 2010, Wang, Lin e Metzler hanno proposto un metodo rivoluzionario per imparare a fare ranking in modo efficiente. La loro soluzione introduce una nuova funzione di costo che ottimizza direttamente una metrica specifica per il trade-off, chiamata **Efficiency-Effectiveness Tradeoff Metric (EET)**. La formula matematica alla base dell'EET per una determinata query è strutturata come segue:

$$EET(Q)=\frac{(1+\beta^{2})\cdot(\gamma(Q)(\sigma(Q))}{(\beta^{2}\cdot)\gamma(Q)+\gamma(Q)}\Rightarrow MEET(R)=\frac{1}{N}\sum EET(Q)$$

Il lavoro si concentra su funzioni di ranking lineari basate su feature e introduce nuove metriche di valutazione dell'efficienza con andamenti a decadimento costante, a gradino (step function), e a decadimento esponenziale. Addestrando i modelli con questa tecnica, gli autori hanno dimostrato una drastica e significativa diminuzione dei tempi medi di esecuzione delle query.

![[Pasted image 20260423151710.png]]

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

![[Pasted image 20260423152019.png]]

### DART: Il Dropout incontra i Regression Trees

Nel 2015, i ricercatori Rashmi e Gilad-Bachrach hanno introdotto l'algoritmo **DART** (pubblicato in PMLR), importando la fortunata tecnica dei *dropouts*, tipica delle reti neurali, nel contesto del Multiple Additive Regression Trees (MART). L'obiettivo primario di questa soluzione è combattere la tendenza dei modelli a sviluppare una **sovra-specializzazione** (*over-specialization*). Sebbene in passato si utilizzasse la tecnica dello *shrinkage* per mitigare questo problema, essa forniva un aiuto senza tuttavia costituire una soluzione definitiva.

DART differisce dal tradizionale MART per due meccanismi fondamentali. In primo luogo, durante la fase in cui il modello sta imparando a costruire un nuovo albero, un sottoinsieme casuale dell'intera struttura viene "silenziato" (*muted*). In secondo luogo, viene applicato uno step di normalizzazione nel momento in cui il nuovo albero viene aggiunto al sistema, così da prevenire il fenomeno dell'**overshooting**. Valutato sul dataset MSLR-Web10K focalizzandosi sulla metrica NDCG@3, DART ha fatto registrare notevoli miglioramenti prestazionali rispetto allo standard LambdaMART.

![[Pasted image 20260423152056.png]]
### X-DART: Fusione tra Dropout e Pruning

Partendo dal successo di DART, Lucchese, Nardini, Orlando, Perego e Trani hanno proposto **X-DART** alla conferenza ACM SIGIR del 2017. Questa evoluzione fonde elegantemente le logiche del dropout con le pratiche di pruning durante l'addestramento. In maniera analoga a DART, anche in X-DART determinati alberi vengono temporaneamente silenziati, ma la peculiarità risiede nel fatto che questo set di nodi viene successivamente rimosso del tutto dopo la fase di *fitting*, qualora ritenuto non necessario.

Questo approccio comporta due importanti vantaggi architetturali. Il primo è che X-DART genera modelli ancora più compatti di DART stesso. Il secondo vantaggio, derivante direttamente dalle ridotte dimensioni del modello, consiste in una minore propensione all'overfitting, il che sblocca il potenziale per raggiungere livelli di efficacia nettamente superiori. Per gestire il processo di rimozione, X-DART prevede tre diverse strategie di potatura: **Ratio**, **Fixed** e **Adaptive**. Le sperimentazioni sui dataset MSLR-Web30K e Istella-S hanno confermato che la variante X-DART (adaptive) fornisce miglioramenti statisticamente significativi rispetto al DART originale, riuscendo a impiegare fino al 20% di alberi in meno. Ancora più impressionante è la capacità del modello di eguagliare la medesima efficacia di DART utilizzando un monte alberi decurtato fino al 40%.

![[Pasted image 20260423152121.png]]

### Calcolo Approssimato dello Score e Cascate Efficienti

Oltre all'ottimizzazione degli alberi stessi, un filone di ricerca parallelo mira al calcolo approssimato dei punteggi mediante l'utilizzo di strutture a cascata molto rapide. Nel 2010, alla conferenza ACM WSDM, Cambazoglu e il suo team (Zaragoza, Chapelle, Chen, Liao, Zheng, Degenhardt) hanno formalizzato il concetto di ottimizzazione tramite "uscite anticipate" (**early exits**) per i sistemi di ranking basati sul machine learning additivo.

La logica dietro a questa strategia del "cortocircuito" in fase di calcolo (*short-circuiting*) si basa su considerazioni empiriche relative all'Information Retrieval. Per ogni singola query sottomessa, esiste solitamente solo una manciata di documenti realmente molto rilevanti, annegati in una moltitudine di risultati totalmente irrilevanti; inoltre, è noto che la stragrande maggioranza degli utenti non si spinge mai oltre la consultazione delle primissime pagine dei risultati. Di conseguenza, è superfluo processare tutti i documenti attraverso l'intero modello matematico. Per risolvere questa inefficienza, Cambazoglu et al. hanno introdotto ensemble additivi capaci di abortire il calcolo preventivamente. Questa interruzione intelligente è governata da quattro specifiche tecniche di soglia: soglie basate sul punteggio (**Score**), sulla capacità (**Capacity**), sul rango (**Rank**) o sulla prossimità (**Proximity**). Implementate all'interno di una piattaforma di machine learning all'avanguardia dotata di alberi GBRT , le ottimizzazioni tramite *early exit* hanno permesso al sistema di operare fino a quattro volte più velocemente rispetto all'algoritmo standard, il tutto senza riscontrare alcuna perdita qualitativa nei risultati proposti all'utente finale.

![[Pasted image 20260423152144.png]]

---

### Concetti Chiave

- **X-CLEAVER**: Evoluzione algoritmica che esegue il pruning e la calibrazione dei pesi direttamente *durante* l'addestramento tramite gradient boosting, creando fin dall'inizio modelli compatti e ad alta efficienza senza dover ricorrere a rielaborazioni successive.

- **DART e X-DART**: Tecniche all'avanguardia che silenziano casualmente parti degli alberi durante il training (dropout) per evitare l'eccessivo adattamento ai dati di addestramento (over-specialization/overfitting). X-DART si distingue per rimuovere permanentemente questi alberi "muti", riducendo enormemente la complessità del modello finale.

- **Early Exits (Uscite Anticipate)**: Strategia computazionale applicata agli ensemble additivi che blocca l'elaborazione del punteggio di rilevanza per quei documenti che mostrano precocemente un basso potenziale. Questo "cortocircuito" matematico previene lo spreco di risorse per risultati palesemente inutili, abbattendo drasticamente i tempi di latenza complessivi.

---
