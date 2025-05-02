using LocaLive.Class;
using LocaLive.Interfaces;
using LocaLive.Services;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Threading.Tasks;

namespace LocaLive.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TicketmasterController : ControllerBase
    {
        private readonly TicketmasterService _ticketmasterService;

        public TicketmasterController(TicketmasterService ticketmasterService)
        {
            _ticketmasterService = ticketmasterService;
        }

        
        [HttpGet("{userId}")]
        public async Task<IActionResult> GetEvents2(int userId, string startDateTime, string endDateTime)
        {
            // tarih verilerini al
            var startDate = DateTime.Parse(startDateTime);
            var endDate = DateTime.Parse(endDateTime);

            // Ticketmaster'dan JSON string al
            var resultMessage = await _ticketmasterService.GetEventsAsync(userId, startDate, endDate);
            if (resultMessage.StartsWith("Error"))
            {
                return BadRequest(resultMessage);
            }

            return Ok(resultMessage);
        }
    }
}
