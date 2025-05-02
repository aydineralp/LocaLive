using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;


namespace LocaLive.Repositorys
{
    public class LocationRepository : ILocationRepository
    {
        private readonly AppDbContext _context;

        public LocationRepository(AppDbContext context)
        {
            _context = context;
        }

        public IEnumerable<Location> GetAllLocations() => _context.Locations.ToList();

        public Location GetLocationById(int id) => _context.Locations.Find(id);

        public void AddLocation(Location location)
        {
            int nextId = _context.Locations.Any() ? _context.Locations.Max(l => l.LocationId) + 1 : 1;
            location.LocationId = nextId;

            _context.Locations.Add(location);
            _context.SaveChanges();
        }

        public void UpdateLocation(Location location)
        {
            var existingLocation = _context.Locations.Find(location.LocationId);

            if (existingLocation != null)
            {

                existingLocation.Country = location.Country;
                existingLocation.City = location.City;
                existingLocation.Latitude = location.Latitude;
                existingLocation.Longitude = location.Longitude;
                existingLocation.IsAutoDetected = location.IsAutoDetected;

                _context.SaveChanges();
            }
        }

        public void DeleteLocation(int id)
        {
            var location = _context.Locations.Find(id);
            if (location != null)
            {
                _context.Locations.Remove(location);
                _context.SaveChanges();
            }
        }
    }
}
