using LocaLive.Class;
using System.Collections.Generic;

namespace LocaLive.Interfaces
{
    public interface IEventRepository
    {
        IEnumerable<Event> GetAllEvents();
        Event GetEventById(int id);
        void AddEvent(Event eventEntity);
        void UpdateEvent(Event eventEntity);
        void DeleteEvent(int id);
        bool EventExists(int id);
        IEnumerable<Event> GetEventsByUserId(int userId);
        void DeleteAllEvents();

    }
}
