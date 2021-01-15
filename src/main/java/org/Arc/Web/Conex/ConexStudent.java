package org.Arc.Web.Conex;

import org.Arc.Client.Student;

public class ConexStudent extends Student {
        private String startTime;

        public ConexStudent(String firstName, String lastName, String startTime) {
            super(null, firstName, lastName);
            this.startTime = startTime;
        }

        public String getTime(){
            return startTime;
        }

        public String toConexString() {
            return "(" + startTime + ") " + getFirstName() + " " + getFirstName();
        }

    }
