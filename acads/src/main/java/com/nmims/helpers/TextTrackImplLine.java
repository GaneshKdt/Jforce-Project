package com.nmims.helpers;

public class TextTrackImplLine {

        String startTime;
        String endTime;
        String text;
        public TextTrackImplLine(String startTime, String endTime, String text) {

            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;

        }
        public String getStartTime() {
            return startTime;
        }
        
        public String getText() {
            return text;
        }
        public String getEndTime() {
            return endTime;
        }

    }

