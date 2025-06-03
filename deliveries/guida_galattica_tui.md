# Guida galattica

Questa guida contiene tutti i comandi necessari per poter giocare a Galaxy Trucker tramite linea di comando.


Convenzioni: 

{_valore_}: indica l'inserimento di un valore numerico nel comando

{**o1/o2**}: indica l'inserimento del valore **o1** oppure **o2** 

_opt_: davanti a un parametro indica che è opzionale

_mult_: davanti a un paramento indica che possono essere più di 1 o nesusno

### Plancia nave
La plancia nave è composta da una serie di caselle riempibili con le tessere del gioco.
Per riferirsi a una cella all'interno dei comandi, bisogna scrivere l'ID della cella.
> 32 è la casella centrale, 04 è la casella in basso a sinistra
```
+-------04------+
|               |
|               |
|               |
|               |
|               |
+---------------+
```

### Plancia di volo
La plancia di volo contiene l'ordine e la posizione
dei giocatori durante il volo. Il giocatore è
identificato tramite uno specifico colore. La plancia di volo
contiene anche la posizione della clessidra.

```
      { }{ }{ }{ }{ }{ }{ }{ }
   { }                        { }
{ }                              { }
{ }                              { }
   { }                        { }
      { }{ }{ }{ }{ }{ }{ }{ }

CLESSIDRA
[        ][        ][        ]
[        ][        ][        ]
[        ][        ][        ]
[        ][        ][        ]
```

### Tessere pescate, prenotate e viste
Queste sono visibili all'interno di contenitori su schermo.
Ogni contenitore ha associato un proprio ID sul lato sinistro.
Questo valore è utilizzato per indirizzare la tessere.

> 0 indica la tessera prenotata, 2 pescata, 3 vista

```
+---PRENOTATE---+   +----PESCATA----+     +--DISPONIBILI--+
0               |   2               |     3               |
|               |   |               |     |               |
|               |   |               |     |               |
|               |   |               |     |               |
|               |   |               |     |               |
+---------------+   +---------------+     +---------------+
```

# Assemblaggio
### Clessidra

> clessidra

Sposta la clessidra nella cella successiva. Il tempo deve essere scaduto.

### Piazza

> piazza {_da_} {_dove_} {_orientamento_}

{_da_}: indica da dove prendere la tessera(prenotato, pescato o visibili)


{_dove_}: dove posizionare la tessera


{_orientamento_}: valore 0-3 che indica quante volte girare in senso orario la tessera prima di piazzarla


Esempio: piazza la tessera pescata nella cella 32 girandola 3 volte
```
piazza 2 32 3
```

### Pronto

> pronto

Comunica al server che l'assemblaggio è terminato. Il server posizionerà automaticamente 
la pedina sulla plancia volo.

### Rimuovi
> rimuovi {_dove_}

Elimina la tessera piazzata sulla plancia nave all'indice specificato.

### Pesca
> pesca

Pesca una nuova tessera dal mazzo delle tessere.

### Prenota
> prenota

Prenota la tessera pescata

# Inizializzazione nave
### Alieni

> alieni _opt_{**no**} _opt_{**v/m**}{_dove_} _opt_{**v/m**}{_dove_}

Comunica dove si vogliono piazzare gli alieni viola (v) o marroni (m).
Se non si vuole piazzar alcun alieno si scrive solamente no.

Esempio
```
alieni no
alieni v42 m32
alieni v02
```

# Carte
### Pesca
> pesca

Pesca una carta dal mazzo. Solo il giocatore più avanti può eseguirlo.

### Getta
> getta {_dove_} _opt_{**r/v/b/g**}

Getta un oggetto alla posizione indicata. Nel caso fosse
una scatola, bisogna indicare anche quale colore si intende gettare.
Gli elementi gettati saranno sommati e utilizzati dalle carte.

### Cannoni
> cannoni {**dim/inc**} {_rotazione_}

Aumenta o diminuisce la potenza di fuoco dei cannoni. 

{_rotazione_} 0-3 indica quante volte è stato ruotato in senso orario il cannone che si intende attivare

# Input per le carte

### Epidemia, Polvere stellare, Spazio aperto
> invia

Non richiedono alcun input oltre ai comandi _getta_ e _cannoni_.
Questo comando dichiara solo stato di pronto.

### Schiavisti, Contrabbandieri
> invia {**si/**no}

Dichiara se si vuole ottenere il premio in caso di vittoria.

### Pianeti
> invia {_pianeta_}

{_pianeta_}: 1-5 indica su quale pianeta si vuole atterrare, 5 sta per _nessun pianeta_

### Meteoriti, Zona di guerra
> invia _mult_{_id asteroidi_}

Indica su quali meteoriti si intende attivare lo scudo.

### Nave abbandonata, Stazione
> invia {**si/no**}

Indica l'intenzione di voler scendere sulla nave o stazione.

### Pirati
> invia {**si/no**} _mult_{_id asteroidi_}

Comunica se si vuole ottenere la ricompensa e da quali asteroidi difendersi.

## Scatole
> scatola _opt_{**no**} {**g/v/r/b**} {_dove_}

Piazza la scatola del colore indicato nella cella.
Se si possono scartare le scatole usando _no_.

# Esci
> esci

Chiude il programma