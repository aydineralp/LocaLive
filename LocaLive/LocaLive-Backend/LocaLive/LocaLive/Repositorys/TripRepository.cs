using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;

namespace LocaLive.Repositorys
{
    public class TripRepository : ITripRepository
    {
        private readonly AppDbContext _context;  

        public TripRepository(AppDbContext context)
        {
            _context = context;

        }


        public IEnumerable<Trip> GetAllTrips()
        {
            return _context.Trips.ToList();

        }

        public List<Trip> GetTripsByUserId(int userId)
        {
            return _context.Trips.Where(trip => trip.UserId == userId).ToList();
        }


        public void AddTrip(Trip trip)
        {
            _context.Trips.Add(trip);
            _context.SaveChanges();
        }

        public void UpdateTrip(Trip trip)
        {
            var existingTrip = _context.Trips.Find(trip.TripId);
            if (existingTrip != null)
            {
                existingTrip.TripName = trip.TripName;
                existingTrip.TripDate = trip.TripDate;
                
                _context.SaveChanges();
            }
        }

        public void DeleteTrip(int id)
        {
            var trip = _context.Trips.Find(id);
            if (trip != null)
            {
                _context.Trips.Remove(trip);
                _context.SaveChanges();
            }
        }

        public bool TripExists(int id)
        {
            return _context.Trips.Any(t => t.TripId == id);
        }


    }
}
