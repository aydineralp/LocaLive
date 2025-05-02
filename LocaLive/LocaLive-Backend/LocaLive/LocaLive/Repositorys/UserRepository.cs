using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using System.Drawing;

namespace LocaLive.Repositorys
{
    public class UserRepository : IUserRepository
    {
        private readonly AppDbContext _context;

        public UserRepository(AppDbContext context)
        {
            _context = context;
        }
        public IEnumerable<User> GetAllUsers()
        {

            return _context.Users;
        }
        public User GetUserById(int id)
        {
            return _context.Users.Find(id);
        }

        public void AddUser(User user)
        {
            var newUser = _context.Users.OrderByDescending(p => p.UserId).FirstOrDefault();
            int newUserId = (newUser != null ? newUser.UserId : 0) + 1;

            if (_context.Users.Any(p => p.UserId == newUserId))
            {
                throw new Exception("Aynı Id'ye sahip başka bir kullanıcı mevcut.");

            }

            user.UserId = newUserId;
            _context.Users.Add(user);
            _context.SaveChanges();
        }

        public void UpdateUser(int id, User updatedUser)
        {
            var user = GetUserById(id);
            if (user != null)
            {
                user.UserName = updatedUser.UserName;
                user.UserSurname = updatedUser.UserSurname;
                user.UserEmail = updatedUser.UserEmail;
                user.UserPassword = updatedUser.UserPassword;
                user.UserDateOfBirth = updatedUser.UserDateOfBirth;
                user.Preferences = updatedUser.Preferences; 
                user.EventRange = updatedUser.EventRange;   

                _context.SaveChanges();
            }
        }


        public void DeleteUser(int id)
        {
            var user = GetUserById(id);
            if (user != null)
            {
                _context.Users.Remove(user);
                _context.SaveChanges();
            }
        }

        public bool UserExists(int id)
        {
            return _context.Users.Any(u => u.UserId == id);
        }


    }
}
