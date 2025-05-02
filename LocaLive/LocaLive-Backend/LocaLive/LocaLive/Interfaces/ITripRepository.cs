using LocaLive.Class;
using System.Drawing;

namespace LocaLive.Interfaces
{
    public interface ITripRepository
    {
        IEnumerable<Trip> GetAllTrips();
        List<Trip> GetTripsByUserId(int id);
        void AddTrip(Trip trip);
        void UpdateTrip(Trip trip);
        void DeleteTrip(int id);
        bool TripExists(int id);
    }
}
