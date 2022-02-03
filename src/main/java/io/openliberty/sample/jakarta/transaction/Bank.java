package io.openliberty.sample.jakarta.transaction;


import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("/Bank")
public class Bank {
	
	@Resource
	UserTransaction utx;

	@PersistenceContext(name="jpa-unit")
	EntityManager em;

	public void deposit(Account a1, Account a2) {
		try {
			utx.begin();
			em.persist(a1);
			em.persist(a2);
			utx.commit();
		} catch (Exception e) {
            System.out.println("Exception in create: " + e.getMessage());
        }
	}
	
	public List<Account> readAllAccounts() {
		return em.createNamedQuery("Account.findAll", Account.class).getResultList();
	}
	
	@GET
	@Path("/account")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> getAccounts() {
		List<Account> accounts = new ArrayList<Account>();
		for (Account p: this.readAllAccounts()) {
			accounts.add(p);
		}
		return accounts;
	}
	
	@POST
	@Path("/{name1}/{balance1}/{name2}/{balance2}")
	public void createNewStudent(@PathParam("name1") String name1, @PathParam("balance1") int balance1, @PathParam("name2") String name2, @PathParam("balance2") int balance2) {
		try {
			Account account1 = new Account(name1, balance1);
			Account account2 = new Account(name2, balance2);
			this.deposit(account1, account2);
		} catch (Exception e) {
			System.out.println("Could not create a new student " + e.getMessage());
		}
		
	}
}
