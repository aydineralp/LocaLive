using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace LocaLive.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class TripController : ControllerBase
    {
        private readonly ITripRepository _tripRepository;

        public TripController(ITripRepository tripRepository)
        {
            _tripRepository = tripRepository;
        }

        
        [HttpGet]
        public IActionResult GetTrips()
        {
            var trips = _tripRepository.GetAllTrips();
            return Ok(trips);
        }

        
        [HttpGet("{id}")]
        public IActionResult GetTripById(int id)
        {
            var trip = _tripRepository.GetTripsByUserId(id);
            if (trip == null)
                return NotFound("Trip not found.");
            return Ok(trip);
        }

        
        [HttpPost]
        public IActionResult AddTrip([FromBody] Trip trip)
        {
            if (trip == null)
                return BadRequest("Invalid trip data.");

            _tripRepository.AddTrip(trip);
            return CreatedAtAction(nameof(GetTripById), new { id = trip.TripId }, trip);
        }

       
        [HttpPut("{id}")]
        public IActionResult UpdateTrip(int id, [FromBody] Trip updatedTrip)
        {
            if (!_tripRepository.TripExists(id))
                return NotFound("Trip not found.");

            updatedTrip.TripId = id; 
            _tripRepository.UpdateTrip(updatedTrip);
            return NoContent();
        }

        
        [HttpDelete("{id}")]
        public IActionResult DeleteTrip(int id) 
        {
            if (!_tripRepository.TripExists(id))
                return NotFound("Trip not found.");

            _tripRepository.DeleteTrip(id);
            return NoContent();
        }
    }
}
