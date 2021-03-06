package org.gnk.tag

import org.gnk.administration.DbCoherenceController

class TagController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max, Integer offset, String sort) {
        max = max ?: 10
        offset = offset ?: 0
        sort = sort ?: 'name'
        params.order = params.order ?: 'asc'

        def resultList = Tag.createCriteria().list(max: max, offset: offset) {
            if (sort.indexOf('tagFamily.') == 0) {
                tagFamily {
                    order(sort.split('\\.')[1], params.order)
                }
            } else
                order(sort, params.order as String)
        }
		
		[ tagInstanceList: resultList ]
    }
	
	def listFrom(String tagFamily) {
		TagFamily family
		List<Tag> result = []
		
		/* Get the TagFamily */
		family = TagFamily.createCriteria().get {
			eq ('value', tagFamily)
		}
		/* Get all Tags corresponding to the TagFamily */
		result = Tag.createCriteria().list {
			eq ('tagFamily', TagFamily)
		}
			
		result
	}

    def create() {
		
        [tagInstance: new Tag(params)]
    }

    def save() {
		if (params.name.equals(""))
		{
			print "Invalid params"
			flash.message = message(code: 'Erreur : Le nom du tag ne peut etre vide !')
            redirect(action: "list")
            return
		}
		
		if (params.TagFamily_select.equals(""))
		{
			flash.message = message(code: 'Erreur : Il faut choisir une famille pour le tag !')
			redirect(action: "list")
			return
		}
		
        Tag tagInstance = new Tag(params)
		String tagFamilyId = params.TagFamily_select
		
		for (Tag tag : Tag.list()) 
		{
			if (tagInstance.name.toLowerCase().equals(tag.name.toLowerCase()))
			{
				print "Tag already exist"
				flash.message = message(code: 'Erreur : Un tag avec le meme nom existe deja !')
	            redirect(action: "list")
	            return
			}
		}

		TagFamily tagFamilyInstance = null;
		tagFamilyInstance = TagFamily.get(tagFamilyId)
		
		if (tagFamilyInstance.equals(null))
		{
			print "Family not found"
			flash.message = message(code: 'Erreur : Cette famille n\'existe pas !')
            redirect(action: "list")
            return
		}

		// Creates the relation between the tag and the family
		tagInstance.tagFamily = tagFamilyInstance
		
        if (!tagInstance.save(flush: true)) {
            render(view: "create", model: [tagInstance: tagInstance])
            return
        }

        
        flash.messageInfo = message(code: 'adminRef.tag.info.create', args: [tagInstance.name, tagFamilyInstance.value])
        redirect(action: "list")
    }

    private void isTagApplicable(Tag tagInstance) {
		// FIXME Changement de base
		/*
        if (params.isPlotTag) {
            PlotTag plotTag = new PlotTag()
            plotTag.tag = tagInstance
            plotTag.save()
        }
        if (params.isRoleTag) {
            RoleTag roleTag = new RoleTag()
            roleTag.tag = tagInstance
            roleTag.save()
        }
        if (params.isResourceTag) {
            ResourceTag resourceTag = new ResourceTag()
            resourceTag.tag = tagInstance
            resourceTag.save()
        }
        */
    }

    def addTagIntoFamily()
	{
		if (params.TagFamily_select.equals("") || params.Tag_select.equals(""))
		{
				print "Invalid params"
				flash.message = message(code: 'Erreur : Il faut choisir un tag et une famille !')
				redirect(action: "list")
				return
		}

		TagFamily tagFamilyInstance = null;
		Tag tagInstance = null;
		for (TagFamily tagFamily : TagFamily.list())
		{
			if (tagFamily.id == params.TagFamily_select.toInteger())
			{
				tagFamilyInstance = tagFamily
				break
			}
		}
		
		for (Tag tag : Tag.list())
		{
			if (tag.id == params.Tag_select.toInteger())
			{
				tagInstance = tag
				break
			}
		}
		
		if (tagFamilyInstance.equals(null) || tagInstance.equals(null))
		{
			print "Family or Tag not found"
			flash.message = message(code: 'Erreur : Tag ou famille invalide !')
			redirect(action: "list")
			return
		}
		
		if (tagInstance.tagFamily.value.equals(tagFamilyInstance.value))
		{
			print "This relation already exists"
			flash.message = message(code: 'Erreur : Cette relation existe deja !')
			redirect(action: "list")
			return
		}
		
		tagInstance.tagFamily = tagFamilyInstance
		tagInstance.save()
		flash.messageInfo = message(code: 'adminRef.tagIntoTagFamily.info.create', args: [tagInstance.name, tagFamilyInstance.value])

		redirect(action: "list")
	}
	
    def show(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        [tagInstance: tagInstance]
    }

    def edit(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        [tagInstance: tagInstance]
    }

    def update(Long id, Long version) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (tagInstance.version > version) {
                tagInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'tag.label', default: 'Tag')] as Object[],
                          "Another user has updated this Tag while you were editing")
                render(view: "edit", model: [tagInstance: tagInstance])
                return
            }
        }

        tagInstance.properties = params

        if (!tagInstance.save(flush: true)) {
            render(view: "edit", model: [tagInstance: tagInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'tag.label', default: 'Tag'), tagInstance.id])
        redirect(action: "show", id: tagInstance.id)
    }

    def delete(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }
		// FIXME Changement de base
		/*
        try {
            tagInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "show", id: id)
        }*/
    }
	
	def deleteFamily(Long idTag, Long idFamily) {
		TagFamily tagFamilyInstance = null;
		Tag tagInstance = null;
		for (TagFamily tagFamily : TagFamily.list())
		{
			if (tagFamily.id == idFamily)
			{
				tagFamilyInstance = tagFamily
				break
			}
		}
		
		for (Tag tag : Tag.list())
		{
			if (tag.id == idTag)
			{
				tagInstance = tag
				break
			}
		}
		
		if (tagFamilyInstance.equals(null) || tagInstance.equals(null))
		{
			print "Family or Tag not found"
			flash.message = message(code: 'Erreur : Famille ou tag incorrecte !')
			redirect(action: "list")
			return
		}
		
		if (tagInstance.tagFamily.id.equals(idFamily) )
		{
			tagInstance.tagFamily = null
            tagFamilyInstance.removeFromTags(tagInstance)
            tagFamilyInstance.save(flush: true)
			tagInstance.save()
			flash.messageInfo = message(code: 'adminRef.tagIntoTagFamily.info.delete', args: [tagInstance.name, tagFamilyInstance.value])
			
			redirect(action: "list")
			return
		}
		
		print("The family was not deleted. Possible error ?")
		redirect(action: "list")
	}
	
	def deleteTag(Long idTag) {

		def tagInstance = Tag.get(idTag)
		print "name " + tagInstance.name
		tagInstance.delete()
		flash.messageInfo = message(code: 'adminRef.tag.info.delete', args: [tagInstance.name])
		redirect(action: "list")
	}
	
	def stats(){
		render(view: "statistics")
	}
}
