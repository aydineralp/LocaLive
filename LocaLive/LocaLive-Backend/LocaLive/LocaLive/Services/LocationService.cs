using LocaLive.Class;
using LocaLive.Interfaces;


namespace LocaLive.Services
{
    public class LocationService
    {
        private readonly ILocationRepository _locationRepository;

        public LocationService(ILocationRepository locationRepository)
        {
            _locationRepository = locationRepository;
        }

        public void AddManualLocation(Location location)
        {
            location.IsAutoDetected = false; // Manuel giriş
            _locationRepository.AddLocation(location);
        }

        public void AddAutoLocation(decimal latitude, decimal longitude)
        {
            var location = new Location
            {
                Country = "DetectedCountry", 
                City = "DetectedCity",
                Latitude = latitude,
                Longitude = longitude,
                IsAutoDetected = true // Otomatik giriş
            };

            _locationRepository.AddLocation(location);
        }
    }
}
