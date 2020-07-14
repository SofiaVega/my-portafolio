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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result = new ArrayList<TimeRange>();
    boolean includeEvent;
    boolean first = true;
    if (request.getDuration() > TimeRange.getTimeInMinutes(23, 59)) {
      return result;
    }
    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    Event lastEvent = new Event("Not an event",
    TimeRange.fromStartDuration(0, 30), Arrays.asList("Me"));
    int lastTime;
    for (Event event : events) {
      includeEvent = false;
      for (String person : request.getAttendees()) {
        if (event.getAttendees().contains(person)) {
          includeEvent = true;
          break;
        }
      }
      if (includeEvent) {
        if (first) {
          if (event.getWhen().start() >= request.getDuration()) {
            result.add(TimeRange.fromStartDuration(0, event.getWhen().start()));
          }
          first = false;
          lastEvent = event;
        } else {
          lastTime = (int) lastEvent.getWhen().start() + lastEvent.getWhen().duration();
          if(event.getWhen().start()-lastTime >= request.getDuration()){
            result.add(TimeRange.fromStartDuration(lastTime, event.getWhen().start() - lastTime));
          }
          if ((int) event.getWhen().start()+event.getWhen().duration() > lastTime) {
            lastEvent = event;
          }
        }
      }
    }
    if (first) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    lastTime = (int) lastEvent.getWhen().start() + lastEvent.getWhen().duration();
    if (lastTime < TimeRange.END_OF_DAY) {
      result.add(TimeRange.fromStartEnd(lastTime, TimeRange.END_OF_DAY, true));
    }
    return result;
  }
}
