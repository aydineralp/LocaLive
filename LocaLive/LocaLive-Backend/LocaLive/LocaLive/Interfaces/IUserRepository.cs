using LocaLive.Class;
using System.Drawing;

namespace LocaLive.Interfaces
{
    public interface IUserRepository
    {
        IEnumerable<User> GetAllUsers();
        User GetUserById(int id);
        void AddUser(User user);
        void UpdateUser(int id,User user);
        void DeleteUser(int id);
        bool UserExists(int id);
       
    }
}
