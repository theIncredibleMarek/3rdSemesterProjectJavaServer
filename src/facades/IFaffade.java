package facades;

import exceptions.NotFoundException;
import model.Users;

public interface IFaffade
{
    public String loginAsJSON(String username, String password) throws NotFoundException;

    public Users addUserFromGson(String json);

    public Users editUserFromGson(String json) throws NotFoundException;

    public Users delete(String username) throws NotFoundException;
}
