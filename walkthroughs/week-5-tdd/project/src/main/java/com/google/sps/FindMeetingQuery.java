// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** Returns a collection of time ranges in which the meeting could fit */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result = new ArrayList<TimeRange>();
    Collection<TimeRange> inclusiveResult = new ArrayList<TimeRange>();
    result = queryMandatory(events, request);
    inclusiveResult = queryAllAttendees(events, request);
    if(inclusiveResult.isEmpty()){
      return result;
    }else{
      return inclusiveResult;
    }
  }
  /** Considers only mandatory attendees */
  private Collection<TimeRange> queryMandatory(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result = new ArrayList<TimeRange>();
    boolean includeEvent;
    boolean noEventsYet = true;
    int lastTime = 0; // Ending time for the last event with mandatory attendees

    // Edgecase for too long of a meeting (takes more than a day)
    if (request.getDuration() > TimeRange.getTimeInMinutes(23, 59)) {
      return result;
    }
    for (Event event : events) {
      includeEvent = false;
      // Check if current event needs to be considered
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          includeEvent = true;
          break;
        }
      }
      if (includeEvent) {
        noEventsYet = false;
        // lastTime is the ending time of the last event
        if(event.getWhen().start() - lastTime >= request.getDuration()){
          result.add(TimeRange.fromStartEnd(lastTime, event.getWhen().start(), false));
        }
        // Updating lastTime is determined by the latest end time, not the latest start time
        if ((int) event.getWhen().start() + event.getWhen().duration() > lastTime) {
          lastTime = (int) event.getWhen().start() + event.getWhen().duration();
        }
      }
    }
    // If there were no events or no events with the requested attendees
    if (noEventsYet) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    // Time range after the last event of the day
    if (TimeRange.END_OF_DAY - lastTime >= request.getDuration()) {
      result.add(TimeRange.fromStartEnd(lastTime, TimeRange.END_OF_DAY, true));
    }
    return result;
  }

  /** Considers all attendees as mandatory */
  private Collection<TimeRange> queryAllAttendees(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result = new ArrayList<TimeRange>();
    boolean includeEvent;
    boolean noEventsYet = true;
    int lastTime = 0; // Ending time for the last event with mandatory attendees

    // Edgecase for too long of a meeting (takes more than a day)
    if (request.getDuration() > TimeRange.getTimeInMinutes(23, 59)) {
      return result;
    }
    for (Event event : events) {
      includeEvent = false;
      // Check if current event needs to be considered
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          includeEvent = true;
          break;
        }
      }
      for (String attendee : request.getOptionalAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          includeEvent = true;
          break;
        }
      }
      if (includeEvent) {
        noEventsYet = false;
        // lastTime is the ending time of the last event
        if(event.getWhen().start() - lastTime >= request.getDuration()){
          result.add(TimeRange.fromStartEnd(lastTime, event.getWhen().start(), false));
        }
        // Updating lastTime is determined by the latest end time, not the latest start time
        if ((int) event.getWhen().start() + event.getWhen().duration() > lastTime) {
          lastTime = (int) event.getWhen().start() + event.getWhen().duration();
        }
      }
    }
    // If there were no events or no events with the requested attendees
    if (noEventsYet) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    // Time range after the last event of the day
    if (TimeRange.END_OF_DAY - lastTime >= request.getDuration()) {
      result.add(TimeRange.fromStartEnd(lastTime, TimeRange.END_OF_DAY, true));
    }
    return result;
  }
}
