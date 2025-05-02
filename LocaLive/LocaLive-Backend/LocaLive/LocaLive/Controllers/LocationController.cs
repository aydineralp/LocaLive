using LocaLive.Interfaces;
using Microsoft.AspNetCore.Mvc;
using LocaLive.Class;
using System;
using System.Collections.Generic;

namespace LocaLive.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LocationController : ControllerBase
    {
        private readonly ILocationRepository _locationRepository;

        public LocationController(ILocationRepository locationRepository)
        {
            _locationRepository = locationRepository;
        }

        
        [HttpGet]
        public IActionResult GetAllLocations()
        {
            try
            {
                var locations = _locationRepository.GetAllLocations();
                return Ok(new Response<IEnumerable<Location>> { Success = true, Data = locations });
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new Response<IEnumerable<Location>>
                    {
                        Success = false,
                        Message = ex.Message
                    });
            }
        }

        
        [HttpGet("{id}")]
        public IActionResult GetLocationById(int id)
        {
            try
            {
                var location = _locationRepository.GetLocationById(id);
                if (location == null)
                    return NotFound(new Response<Location>
                    {
                        Success = false,
                        Message = "Lokasyon bulunamadı."
                    });

                return Ok(new Response<Location>
                {
                    Success = true,
                    Data = location
                });
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new Response<Location>
                    {
                        Success = false,
                        Message = ex.Message
                    });
            }
        }

        
        [HttpPost]
        public IActionResult AddLocation([FromBody] Location location)
        {
            try
            {
                if (location == null)
                    return BadRequest(new Response<Location>
                    {
                        Success = false,
                        Message = "Geçersiz lokasyon değeri"
                    });

                _locationRepository.AddLocation(location);
                return CreatedAtAction(nameof(GetLocationById), new { id = location.LocationId },
                    new Response<Location>
                    {
                        Success = true,
                        Data = location
                    });
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new Response<Location>
                    {
                        Success = false,
                        Message = ex.Message
                    });
            }
        }

       
        [HttpPut("{id}")]
        public IActionResult UpdateLocation(int id, [FromBody] Location updatedLocation)
        {
            try
            {
                var existingLocation = _locationRepository.GetLocationById(id);
                if (existingLocation == null)
                    return NotFound(new Response<Location>
                    {
                        Success = false,
                        Message = "Location not found."
                    });

                updatedLocation.LocationId = id; 
                _locationRepository.UpdateLocation(updatedLocation);
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new Response<Location>
                    {
                        Success = false,
                        Message = ex.Message
                    });
            }
        }

       
        [HttpDelete("{id}")]
        public IActionResult DeleteLocation(int id)
        {
            try
            {
                var existingLocation = _locationRepository.GetLocationById(id);
                if (existingLocation == null)
                    return NotFound(new Response<Location>
                    {
                        Success = false,
                        Message = "Location not found."
                    });

                _locationRepository.DeleteLocation(id);
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new Response<Location>
                    {
                        Success = false,
                        Message = ex.Message
                    });
            }
        }
    }
}
