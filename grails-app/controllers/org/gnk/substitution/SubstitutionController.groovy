package org.gnk.substitution

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.gnk.parser.GNKDataContainerService
import org.gnk.gn.Gn
import org.gnk.parser.gn.GnXMLWriterService

class SubstitutionController {

    String xmlGnTestPath = "DTD_V3.xml"

    def index() {
        InputHandler inputHandler = new InputHandler()
        final gnIdStr = params.gnId
        if (gnIdStr == null || !(gnIdStr as String).isInteger()) {
            //redirect(action: "list", controller: "selectIntrigue", params: params)
            //return
            String fileContent = new File(xmlGnTestPath).text
            inputHandler.parseGN(fileContent)
        }
        else {
            Integer gnDbId = gnIdStr as Integer;

            Gn gn = Gn.get(gnDbId)
            inputHandler.parseGN(gn)

            /*render(text: gn.getDtd(), contentType: "text/xml", encoding: "UTF-8")
            return*/
        }

        [gnInfo : inputHandler.gnInfo,
        characterList : inputHandler.characterList,
        resourceList : inputHandler.resourceList,
        placeList : inputHandler.placeList,
        pastsceneList : inputHandler.pastsceneList,
        eventList : inputHandler.eventList]
    }

    def getSubCharacters() {
        JSONObject charJSONObject = request.JSON

        IntegrationHandler integrationHandler = new IntegrationHandler()
        charJSONObject = integrationHandler.namingIntegration(charJSONObject)

        render charJSONObject
    }

    def getSubResources() {
        JSONObject resourceJSONObject = request.JSON

        IntegrationHandler integrationHandler = new IntegrationHandler()
        resourceJSONObject = integrationHandler.resourceIntegration(resourceJSONObject)

        render resourceJSONObject
    }

    def getSubPlaces() {
        JSONObject placeJSONObject = request.JSON

        IntegrationHandler integrationHandler = new IntegrationHandler()
        placeJSONObject = integrationHandler.placeIntegration(placeJSONObject)

        render placeJSONObject
    }

    def getSubDates() {
        JSONObject dateJSONObject = request.JSON

        IntegrationHandler integrationHandler = new IntegrationHandler()
        dateJSONObject = integrationHandler.dateIntegration(dateJSONObject)

        render dateJSONObject
    }

    def validateSubstitution() {
        JSONObject subJSON = JSON.parse(params.subJSON)

        Integer gnDbId = subJSON.gnDbId as Integer

        // Reader
        GNKDataContainerService gnkDataContainerService = new GNKDataContainerService()
        if (gnDbId == -1) {
            String fileContent = new File(xmlGnTestPath).text
            gnkDataContainerService.ReadDTD(fileContent)
        }
        else {
            gnkDataContainerService.ReadDTD(Gn.get(gnDbId))
        }

        // Output Substitution
        OutputHandler outputHandler = new OutputHandler()
        // Characters
        JSONArray charsJSONArray = subJSON.subCharacter
        outputHandler.updateGnWithNaming(gnkDataContainerService, charsJSONArray)
        // Resources
        JSONArray resourcesJSONArray = subJSON.subResource
        outputHandler.updateGnWithResources(gnkDataContainerService, resourcesJSONArray)
        // Places
        JSONArray placesJSONArray = subJSON.subPlace
        outputHandler.updateGnWithPlaces(gnkDataContainerService, placesJSONArray)
        // Dates
        JSONObject datesJSON = subJSON.subDate
        outputHandler.updateGnWithDates(gnkDataContainerService, datesJSON)

        // Writer
        GnXMLWriterService gnXMLWriter = new GnXMLWriterService()
        String xmlGN = gnXMLWriter.getGNKDTDString(gnkDataContainerService.gn)

        if (gnDbId == -1) {
            render(text: xmlGN, contentType: "text/xml", encoding: "UTF-8")
        }
        else {
            //render(text: xmlGN, contentType: "text/xml", encoding: "UTF-8")
            //return
            // Save in DataBase
            gnkDataContainerService.gn.dtd = xmlGN;
            if (!gnkDataContainerService.gn.save(flush: true)) {
                redirect(action: "list", controller: "selectIntrigue", params: [gnId: gnDbId])
                return
            }

            // Go to publication
            redirect(controller: "publication", action: "publication", params: [gnId: gnDbId])
        }
    }
}
