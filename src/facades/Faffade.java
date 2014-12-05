package facades;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.Users;
import security.SecurePassword;

public class Faffade implements IFaffade
{

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("Semester3JavaServerPU");
    private EntityManager em;
    private SecurePassword sPass;
    private Gson gson = new Gson();

    public Faffade()
    {
    }

    @Override
    public String loginAsJSON(String username, String password) throws NotFoundException
    {
        //TODO: add login through DB
        try
        {
            em = emf.createEntityManager();
            Users u = em.find(Users.class, username);
            if (u == null)
            {
                throw new NotFoundException("No user exists for the given username");
            }
            //User exists so now secure his password and compare with the one in the DB
            String passFromWeb = SecurePassword.getPassword(password, u.getSalt());
            if (passFromWeb.equals(u.getPassword()))
            {
                //login successful
                return gson.toJson(u.getType());
            } else
            {
                //wrong password
                return gson.toJson("Wrong password.");
            }
        } catch (Exception e)
        {
            //in case of exception return null object
            return gson.toJson(e);
        }
    }

    @Override
    public Users addUserFromGson(String json)
    {
        try
        {
            Users u = gson.fromJson(json, Users.class);
            em = emf.createEntityManager();
            //generate the password and the salt and insert into the db
            String salt = SecurePassword.generateSalt();
            String securePassword = SecurePassword.getPassword(u.getPassword(), salt);
            u.setPassword(securePassword);
            u.setSalt(salt);
            em.getTransaction().begin();
            em.persist(u);
            em.getTransaction().commit();
            return u;
        } catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public Users editUserFromGson(String json) throws NotFoundException
    {
        try
        {
            em = emf.createEntityManager();
            Users updatedUser = gson.fromJson(json, Users.class);
            em.getTransaction().begin();
            Users existingUser = em.find(Users.class, updatedUser.getUsername());
            if (existingUser == null)
            {
                throw new NotFoundException("No user exists for the given id");
            }
            
            String salt = SecurePassword.generateSalt();
            String password = SecurePassword.getPassword(updatedUser.getPassword(), salt);
            existingUser.setPassword(password);
            existingUser.setSalt(salt);
            existingUser.setType(updatedUser.getType());
            em.getTransaction().commit();
            return updatedUser;
        } catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public Users delete(String username) throws NotFoundException
    {
        em = emf.createEntityManager();
        Users u = em.find(Users.class, username);
        if (u == null)
        {
            throw new NotFoundException("No person exists for the given id");
        }
        em.getTransaction().begin();
        em.remove(u);
        em.getTransaction().commit();
        return u;
    }

}
