using LocaLive.Class;
using LocaLive.Context;
using LocaLive.Interfaces;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace LocaLive.Repositorys
{
    public class EventRepository : IEventRepository
    {
        private readonly AppDbContext _context;

        public EventRepository(AppDbContext context)
        {
            _context = context;
        }

        public IEnumerable<Event> GetAllEvents()
        {
            return _context.Events.ToList();
        }

        public Event GetEventById(int id)
        {
            return _context.Events.Find(id);
        }

        public void AddEvent(Event eventEntity)
        {
            // Mevcut en büyük EventId'yi bul ve bir artır
            int newEventId = _context.Events.Any()
                ? _context.Events.Max(e => e.EventId) + 1
                : 1;

            // Yeni EventId'yi ata
            eventEntity.EventId = newEventId;

            // Yeni eventi ekle ve kaydet
            _context.Events.Add(eventEntity);
            _context.SaveChanges();
        }


        public void UpdateEvent(Event eventEntity)
        {
            var existingEvent = _context.Events.Find(eventEntity.EventId);
            if (existingEvent != null)
            {
                existingEvent.EventName = eventEntity.EventName;
                existingEvent.EventDescription = eventEntity.EventDescription;
                existingEvent.EventDate = eventEntity.EventDate;
                existingEvent.Longitude = eventEntity.Longitude;
                existingEvent.Latitude = eventEntity.Latitude;
                existingEvent.EventUrl = eventEntity.EventUrl;
                _context.SaveChanges();
            }
        }

        public void DeleteEvent(int id)
        {
            var eventEntity = _context.Events.Find(id);
            if (eventEntity != null)
            {
                _context.Events.Remove(eventEntity);
                _context.SaveChanges();
            }
        }

        public bool EventExists(int id)
        {
            return _context.Events.Any(e => e.EventId == id);
        }

        public IEnumerable<Event> GetEventsByUserId(int userId)
        {
            return _context.Events.Where(e => e.UserId == userId).ToList();
        }

        public void DeleteAllEvents()
        {
            var allEvents = _context.Events.ToList();
            _context.Events.RemoveRange(allEvents);
            _context.SaveChanges();
        }

    }
}
