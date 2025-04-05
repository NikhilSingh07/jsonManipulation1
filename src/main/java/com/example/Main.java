package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * 
 * Performed 1. Grouping (based on countries) +  2. Transforming String date to Localdate
*/

public class Main {
    public static void main(String[] args) throws Exception {

        String jsonData = getJson();
        // parse the json data to java object
        ObjectMapper mapper = new ObjectMapper();

        PartnerWrapper partnerWrapper = mapper.readValue(jsonData, PartnerWrapper.class);

        Map<String, List<Partner>> groupedCountries = new HashMap<>();

        // 1. GROUPING DATA BASED ON COUNTRY
        groupedCountries = partnerWrapper.partners.stream().collect(Collectors.groupingBy(Partner::country));
    

        List<Country> countries = new ArrayList<>();

        for(Map.Entry<String, List<Partner>> mapEntry:groupedCountries.entrySet()) {

            String countryName = mapEntry.getKey();
            List<Partner> partners = mapEntry.getValue();
            Map<LocalDate, Set<String>> attendees = new TreeMap<>();

            // 2. Finding partners in each country available for 2 consecutive days: LocalDate -> [List of attendees]-> Treeset
            for(Partner partner: partners) {

                // TRANSFORMING STRING DATE TO LOCAL DATE
                List<LocalDate> dates= partner.availableDates.stream().map(LocalDate::parse).toList();
                for (int i = 0; i < dates.size()-1; i++) {
                    
                    LocalDate curDate = dates.get(i);
                    LocalDate nexDate = dates.get(i+1);

                    if(curDate.plusDays(1).equals(nexDate)){

                        // Found an attendee
                        if(!attendees.containsKey(curDate)){
                            attendees.put(curDate, new HashSet<>());
                        } 
                        attendees.get(curDate).add(partner.email);
                    }
                }
            }

            // 3. picking earliest date with most attendees

            LocalDate bestDate = null;
            int maxAttendee = 0;
            Set<String> attendeeEmails = new HashSet<>();

            for(Map.Entry<LocalDate, Set<String>> e :attendees.entrySet()) {

                if(e.getValue().size() > maxAttendee) {
                    maxAttendee = e.getValue().size();
                    bestDate = e.getKey();
                    attendeeEmails = e.getValue();
                }
            }

            countries.add(new Country(maxAttendee, attendeeEmails.stream().toList(), countryName, bestDate!=null?bestDate.toString():null));
        }

        
        CountryWrapper wrapper = new CountryWrapper(countries);
        String jsonRes = mapper.writeValueAsString(countries);
        System.out.println(jsonRes);

    }

    /*
     * 1. Grouping data based on countries: country -> [List of Partners...]
     * 2. Finding partners in each country available for 2 consecutive days: LocalDate -> [List of attendees]-> Treeset
     * 3. Pick earliest date with most attendees
     * 4. In the end we will have: name, atendee count, attendee emails, start date
     * 5. Save it in Country instance
     */

    private static record PartnerWrapper(List<Partner> partners) {}
    private static record Partner(String firstName, String lastName, String email, String country, List<String> availableDates) {}

    private static record CountryWrapper(List<Country> countries) {}
    private static record Country(int attendeeCount, List<String> attendees, String name, String startDate){}

        /*
     * There's an 2 day elvent in each country, we need the best/earliest start date with a max possible antendee count
     * 
     * {
            "countries": [
          {
            "attendeeCount": 1,
            "attendees": [
            "cbrenna@hubspotpartners.com"
            ],
            "name": "Ireland",
            "startDate": "2017-04-29"
          },
          {
            "attendeeCount": 0,
            "attendees": [],
            "name": "United States",
            "startDate": null
          },
          {
            "attendeeCount": 3,
            "attendees": [
            "omajica@hubspotpartners.com",
            "taffelt@hubspotpartners.com",
            "tmozie@hubspotpartners.com"
            ],
            "name": "Spain",
            "startDate": "2017-04-28"
          }
            ]
          }
     */
    private static String getJson(){

        // allows to create multiline string w/o adding \n
        String json = """
                        {
                            "partners": [
                        {
                            "firstName": "Darin",
                            "lastName": "Daignault",
                            "email": "ddaignault@hubspotpartners.com",
                            "country": "United States",
                            "availableDates": [
                            "2017-05-03",
                            "2017-05-06"
                            ]
                        },
                        {
                            "firstName": "Crystal",
                            "lastName": "Brenna",
                            "email": "cbrenna@hubspotpartners.com",
                            "country": "Ireland",
                            "availableDates": [
                            "2017-04-27",
                            "2017-04-29",
                            "2017-04-30"
                            ]
                        },
                        {
                            "firstName": "Janyce",
                            "lastName": "Gustison",
                            "email": "jgustison@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-29",
                            "2017-04-30",
                            "2017-05-01"
                            ]
                        },
                        {
                            "firstName": "Tifany",
                            "lastName": "Mozie",
                            "email": "tmozie@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-28",
                            "2017-04-29",
                            "2017-05-01",
                            "2017-05-04"
                            ]
                        },
                        {
                            "firstName": "Temple",
                            "lastName": "Affelt",
                            "email": "taffelt@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-28",
                            "2017-04-29",
                            "2017-05-02",
                            "2017-05-04"
                            ]
                        },
                        {
                            "firstName": "Robyn",
                            "lastName": "Yarwood",
                            "email": "ryarwood@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-29",
                            "2017-04-30",
                            "2017-05-02",
                            "2017-05-03"
                            ]
                        },
                        {
                            "firstName": "Shirlene",
                            "lastName": "Filipponi",
                            "email": "sfilipponi@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-30",
                            "2017-05-01"
                            ]
                        },
                        {
                            "firstName": "Oliver",
                            "lastName": "Majica",
                            "email": "omajica@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-28",
                            "2017-04-29",
                            "2017-05-01",
                            "2017-05-03"
                            ]
                        },
                        {
                            "firstName": "Wilber",
                            "lastName": "Zartman",
                            "email": "wzartman@hubspotpartners.com",
                            "country": "Spain",
                            "availableDates": [
                            "2017-04-29",
                            "2017-04-30",
                            "2017-05-02",
                            "2017-05-03"
                            ]
                        },
                        {
                            "firstName": "Eugena",
                            "lastName": "Auther",
                            "email": "eauther@hubspotpartners.com",
                            "country": "United States",
                            "availableDates": [
                            "2017-05-04",
                            "2017-05-09"
                            ]
                        }
                            ]
                        }
                """;
        
        return json;        
    }



}