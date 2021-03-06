package org.gnk.substitution

import org.gnk.parser.GNKDataContainerService
import org.gnk.resplacetime.GenericPlace
import org.gnk.resplacetime.GenericPlaceHasTag
import org.gnk.resplacetime.GenericResource
import org.gnk.resplacetime.GenericResourceHasTag
import org.gnk.roletoperso.RoleHasEvent
import org.gnk.roletoperso.RoleHasEventHasGenericResource
import org.gnk.substitution.data.*;
import org.gnk.gn.Gn

class InputHandler {

    GnInformation gnInfo
    List<Character> characterList
    List<Resource> resourceList
    List<Place> placeList
    List<Pastscene> pastsceneList
    List<Event> eventList

    public void parseGN(String gnString) {
        // Reader
        GNKDataContainerService gnkDataContainerService = new GNKDataContainerService()
        gnkDataContainerService.ReadDTD(gnString)
        Gn gnInst = gnkDataContainerService.gn
        gnInst.id = -1
        createData(gnInst)
    }

    public void parseGN(Gn gn) {
        // Reader
        GNKDataContainerService gnkDataContainerService = new GNKDataContainerService()
        gnkDataContainerService.ReadDTD(gn)
        Gn gnInst = gnkDataContainerService.gn
        createData(gnInst)
    }

    private createData(Gn gnInst) {
        // GnInformation construction
        createGnInformation(gnInst)

        // CharacterList construction
        createCharacterList(gnInst)

        // ResourceList construction
        createResourceList(gnInst)

        // PlaceList construction
        createPlaceList(gnInst)

        // PastsceneList construction
        createPastsceneList(gnInst)

        // EventList construction
        createEventList(gnInst)
    }

    // GnInfo
    private void createGnInformation(Gn gnInst) {
        gnInfo = new GnInformation()

        // Database id
        gnInfo.dbId = gnInst.id
        // Title
        gnInfo.title = gnInst.name
        // Creation date
        gnInfo.creationDate = gnInst.dateCreated
        // Last update date
        gnInfo.lastUpdateDate = gnInst.lastUpdated
        // Nb players
        gnInfo.nbPlayers = gnInst.nbPlayers
        // Universe
        gnInfo.universe = gnInst.univers.name
        // T0 date
        gnInfo.t0Date = gnInst.t0Date
        // Duration
        gnInfo.duration = gnInst.duration
        // Tags
        gnInfo.tagList = []
        for (el in gnInst.gnTags) {
            Tag tagData = new Tag()
            tagData.value = el.key.name
            tagData.family = el.key.tagFamily.value
            tagData.weight = el.value
            gnInfo.tagList.add(tagData)
        }
    }

    // CharacterList
    private void createCharacterList(Gn gnInst) {
        characterList = []

        for(character in gnInst.characterSet) {
            Character characterData = new Character()

            // Id
            characterData.id = character.DTDId
            // Gender
            characterData.gender = character.gender
            // Type
            characterData.type = character.type

            // TagList
            characterData.tagList = []
            for (el in character.getTags()) {
                Tag tagData = new Tag()
                tagData.value = el.key.name
                tagData.family = el.key.tagFamily.value
                tagData.weight = el.value
                characterData.tagList.add(tagData)
            }

            // RoleList
            characterData.roleList = []

            // RelationList
            characterData.relationList = []

            characterList.add(characterData)
        }
        for(character in gnInst.nonPlayerCharSet) {
            Character characterData = new Character()

            // Id
            characterData.id = character.DTDId
            // Gender
            characterData.gender = character.gender
            // Type
            characterData.type = character.type

            // TagList
            characterData.tagList = []
            for (el in character.getTags()) {
                Tag tagData = new Tag()
                tagData.value = el.key.name
                tagData.family = el.key.tagFamily.value
                tagData.weight = el.value
                characterData.tagList.add(tagData)
            }

            // RoleList
            characterData.roleList = []

            // RelationList
            characterData.relationList = []

            characterList.add(characterData)
        }
    }

    // ResourceList
    private void createResourceList(Gn gnInst) {
        resourceList = []

        // Iterate generic resources
        for(plot in gnInst.selectedPlotSet) {
            String plotId = plot.DTDId as String
            for(org.gnk.roletoperso.Role role : plot.roles) {
                if (role.roleHasEvents) {
                    for (RoleHasEvent roleHasEvent : role.roleHasEvents) {
                        if (roleHasEvent.roleHasEventHasGenericResources) {
                            for (RoleHasEventHasGenericResource roleHasEventHasGenericResource : roleHasEvent.roleHasEventHasGenericResources) {
                                GenericResource genericResource = roleHasEventHasGenericResource.genericResource

                                Resource resource = new Resource()
                                // Id
                                resource.id = genericResource.DTDId
                                // Plot id
                                resource.plotId = plotId
                                // Code
                                resource.code = genericResource.code
                                // Comment
                                resource.comment = genericResource.comment

                                // TagList
                                resource.tagList = []
                                if (genericResource.extTags) {
                                    for (GenericResourceHasTag genericResourceHasTag : genericResource.extTags) {
                                        Tag tagData = new Tag()

                                        tagData.value = genericResourceHasTag.tag.name
                                        tagData.family = genericResourceHasTag.tag.tagFamily.value
                                        tagData.weight = genericResourceHasTag.weight as Integer

                                        resource.tagList.add(tagData)
                                    }
                                }
                                resourceList.add(resource)
                            }
                        }
                    }
                }
            }
        }
    }

    // PlaceList
    private void createPlaceList(Gn gnInst) {
        placeList = []

        for(plot in gnInst.selectedPlotSet) {
            String plotId = plot.DTDId as String
            for(pastScene in plot.pastescenes) {
                GenericPlace genericPlace = pastScene.genericPlace
                if (genericPlace != null && !isGenericPlaceInList(placeList, plotId, genericPlace.DTDId as String)) {
                    placeList.add(createPlace(genericPlace, plotId))
                }
            }
            for(event in plot.events) {
                GenericPlace genericPlace = event.genericPlace
                if (genericPlace != null && !isGenericPlaceInList(placeList, plotId, genericPlace.DTDId as String)) {
                    placeList.add(createPlace(genericPlace, plotId))
                }
            }
        }
    }

    private Boolean isGenericPlaceInList(List<Place> placeList, String plotId, String genericPlaceId) {
        for(genericPlace in placeList) {
            if (genericPlace.plotId == plotId && genericPlace.id == genericPlaceId) {
                return true;
            }
        }
        return false;
    }

    private Place createPlace(GenericPlace genericPlace, String plotId) {
        Place place = new Place();
        // Id
        place.id = genericPlace.DTDId
        // Plot id
        place.plotId = plotId
        // Code
        place.code = genericPlace.code
        // Comment
        place.comment = genericPlace.comment

        // TagList
        place.tagList = []
        if (genericPlace.extTags) {
            for (GenericPlaceHasTag genericPlaceHasTag : genericPlace.extTags) {
                Tag tagData = new Tag()

                tagData.value = genericPlaceHasTag.tag.name
                tagData.family = genericPlaceHasTag.tag.tagFamily.value
                tagData.weight = genericPlaceHasTag.weight as Integer

                place.tagList.add(tagData)
            }
        }

        // Plot
        place.plot = null

        return place
    }

    // Pastscene
    private void createPastsceneList(Gn gnInst) {
        pastsceneList = []

        for(plot in gnInst.selectedPlotSet) {
            String plotId = plot.DTDId as String
            for(pastscene in plot.pastescenes) {
                Pastscene pastsceneData = new Pastscene()

                // Id
                pastsceneData.id = pastscene.DTDId
                // Plot id
                pastsceneData.plotId = plotId
                //Plot name
                pastsceneData.plotName = plot.name
                // Title
                pastsceneData.title = pastscene.title
                // Relative time
                pastsceneData.relativeTime = pastscene.timingRelative
                pastsceneData.relativeTimeUnit = pastscene.unitTimingRelative
                // Absolute time
                pastsceneData.absoluteYear = pastscene.dateYear
                pastsceneData.absoluteMonth = pastscene.dateMonth
                pastsceneData.absoluteDay = pastscene.dateDay
                pastsceneData.absoluteHour = pastscene.dateHour
                pastsceneData.absoluteMin = pastscene.dateMinute

                pastsceneList.add(pastsceneData)
            }
        }
    }

    // Event
    private void createEventList(Gn gnInst) {
        eventList = []

        for(plot in gnInst.selectedPlotSet) {
            String plotId = plot.DTDId as String
            for(event in plot.events) {
                Event eventData = new Event()

                // Id
                eventData.id = event.DTDId
                // Plot id
                eventData.plotId = plotId
                //Plot name
                eventData.plotName = plot.name
                // Title
                eventData.title = event.name
                // Is planned
                eventData.isPlanned = event.isPlanned
                // Timing
                eventData.timing = event.timing

                eventList.add(eventData)
            }
        }
    }
}
