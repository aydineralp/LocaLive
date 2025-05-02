using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Drawing;
using LoginRequest = LocaLive.Class.LoginRequest;

namespace LocaLive.Controllers
{
    [Route("api/User")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IUserRepository _userRepository;

        public UserController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

       
        [HttpGet]
        public ActionResult<Response<IEnumerable<User>>> GetAllUsers()
        {
            try
            {
                var users = _userRepository.GetAllUsers();
                return Ok(users);
            }
            catch (System.Exception ex) 
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new Response<IEnumerable<User>> { Success = false, Message = ex.Message });
            }
        }


        [HttpPost] 
        public async Task<ActionResult<Response<User>>> AddUser(User user)
        {
            try
            {
                _userRepository.AddUser(user);
                return Ok(new Response<User> { Success = true, Data = user });
            }
            catch (System.Exception ex)
            {
                return Conflict(new Response<User> { Success = false, Message = ex.Message });
            }
        }

        [HttpGet("{id}")]
        public ActionResult<Response<User>> GetUserById(int id) 
        {
            try
            {
                var user = _userRepository.GetUserById(id);

                if (user == null)
                {
                    return NotFound(new Response<User> { Success = false, Message = $"{id} id'li kullanıcı bulunamadı" });
                }

                return Ok(new Response<User> { Success = true, Data = user });

            }
            catch (System.Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new Response<User> { Success = false, Message = ex.Message });
            }
        }

        
       

       
        [HttpPut("{id}")]

        public ActionResult<Response<User>> UpdateUser(int id, User updatedUser)
        {
            if (!_userRepository.UserExists(id))
                return NotFound("User not found.");

            updatedUser.UserId = id;  
            _userRepository.UpdateUser(id,updatedUser);
            return NoContent();
        }

       
        [HttpDelete("{id}")]
        public IActionResult DeleteUser(int id)
        {
            if (!_userRepository.UserExists(id))
                return NotFound("User not found.");

            _userRepository.DeleteUser(id);
            return NoContent();
        }

        [HttpPost("Login")]
        public ActionResult<Response<object>> Login([FromBody] LoginRequest loginRequest)
        {
            try
            {
                // kullanıcıyı e-posta ile bul
                var user = _userRepository.GetAllUsers().FirstOrDefault(u => u.UserEmail == loginRequest.Email);

                if (user == null)
                {
                    return NotFound(new Response<object>
                    {
                        Success = false,
                        Message = "Kullanıcı bulunamadı."
                    });
                }

                // Şifre doğrulama
                if (user.UserPassword != loginRequest.Password)
                {
                    
                    return Unauthorized();
                }

                // Başarılı giriş yanıtı
                return Ok(new Response<object>
                {
                    Success = true,
                    Message = "Giriş başarılı.",
                    Data = new
                    {
                        userId = user.UserId,
                        userName = user.UserName
                    }
                });
            }
            catch (System.Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new Response<object>
                {
                    Success = false,
                    Message = ex.Message
                });
            }
        }



        [HttpPatch("{id}/preferences")]
        public IActionResult UpdateUserPreferences(int id, [FromBody] string newPreferences)
        {
            try
            {
                var user = _userRepository.GetUserById(id);

                if (user == null)
                {
                    return NotFound(new Response<User> { Success = false, Message = "User not found." });
                }

                user.Preferences = newPreferences;
                _userRepository.UpdateUser(id, user);

                return Ok(new Response<User> { Success = true, Data = user });
            }
            catch (System.Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new Response<User> { Success = false, Message = ex.Message });
            }
        }



    }

}

