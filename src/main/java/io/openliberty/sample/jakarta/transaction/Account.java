package io.openliberty.sample.jakarta.transaction;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Entity
@Table(name = "Account")
@NamedQuery(name="Account.findAll", query="Select a FROM Account a")
public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;
	
	@NotNull
    @Column(name="name")
	private String name;
	
	@Positive
    private int balance;
	
	public long getId() {
        return id;
    }
        
    public String getName() {
    	return this.name;
    }
    
    public int getBalance() {
    	return this.balance;
    }
    
    public Account() {
    }

    public Account(String name, int balance) {
    	this.name = name;
        this.balance = balance;
    }

}
