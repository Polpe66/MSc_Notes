// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract Created {
    uint public x;

    // Il costruttore riceve 'a' e lo salva in 'x'. 
    // È 'payable' quindi può ricevere Ether al momento della nascita.
    constructor(uint a) payable {
        x = a;
    }

    function increment() public {
        x += 1;
    }

    function get() public view returns (uint) {
        return x;
    }
}

contract Creator {
    // CORRETTO: rimosso typo 'innerContarct' -> 'innerContract'
    Created public innerContract; 

    // Crea un nuovo contratto passandogli solo il dato 'arg'
    function createCreated(uint arg) public {
        Created newCreated = new Created(arg);
        innerContract = newCreated;
    }

    // Si collega a un contratto già esistente tramite il suo indirizzo
    function overrideCreated(address arg) public {
        innerContract = Created(arg);
    }

    // Legge il valore di x dal contratto figlio salvato
    function getInnerContractX() public view returns (uint) {
        return innerContract.x();
    }

    // Chiama la funzione increment() sul contratto figlio
    function incrementInnerContractX() public {
        innerContract.increment();
    }

    // Crea il contratto inviandogli ANCHE degli Ether
    function createAndEndowCreated(uint arg, uint amount) public payable {
        // NOTA: 'amount' deve essere disponibile nel bilancio di questo contratto.
        // Se l'utente invia gli ether in questa transazione, 'amount' dovrebbe essere msg.value
        Created newCreated = new Created{value: amount}(arg);
        
        // CORRETTO: rimosso 'newCreated.x();' perché non faceva nulla.
        // Ora aggiorniamo il riferimento principale così possiamo usarlo dopo
        innerContract = newCreated;
    }
}
