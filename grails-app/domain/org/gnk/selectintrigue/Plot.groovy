package org.gnk.selectintrigue
import org.gnk.resplacetime.Event
import org.gnk.resplacetime.Pastscene
import org.gnk.roletoperso.Role
import org.gnk.tag.Tag
import org.gnk.tag.TagFamily
import org.gnk.tag.Univers
import org.gnk.user.User

class Plot {

    Integer id
    Integer version

	Date lastUpdated
	Date dateCreated
    private static List<Tag> tagList
    public static List getTagList() {
        tagList = new ArrayList<Tag>()
        for(Tag tag : Tag.list())
        {
            if (tag.tagFamily.relevantPlot)
                tagList.add(tag)
        }

        return tagList
    }


	String name
  //  float plotVersion

	Boolean isEvenemential
	Boolean isMainstream
	Boolean isPublic
	Boolean isDraft
	Date creationDate
	Date updatedDate
	String description
	User user

	static transients = ["roleListBuffer", "sumPipRolesBuffer", "plotHasPlotTagListBuffer", "DTDId"]

	Integer sumPipRolesBuffer;
    Integer DTDId;

	static hasMany = [ events: Event,
                       extTags: PlotHasTag,
                       plotHasUniverses: PlotHasUnivers,
                       roles: Role,
                       pastescenes: Pastscene]

	static belongsTo = User

	static constraints = {
        name maxSize: 45
    }
	
	static mapping = {
        autoTimestamp true
		description type: 'text'
        id type:'integer'
        version type: 'integer'
        events cascade: 'all-delete-orphan'
        plotHasUniverses cascade: 'all-delete-orphan'
        roles cascade: 'all-delete-orphan'
        extTags cascade: 'all-delete-orphan'
        pastescenes cascade: 'all-delete-orphan'
	}

    public Set<Role> getterRoles (){
        return roles;
    }

    public void addARole(Role role) {
        if (roles == null) {
            roles = new HashSet<Role>()
        }
        roles.add(role)
        role.plot = this
    }

	public int getTagWeight (Tag plotTag){
		for(PlotHasTag plotHasPlotTag : extTags) {
			if (plotHasPlotTag.tag == plotTag)
				return plotHasPlotTag.weight
		}
		return 0;
	}
	
	public int getSumPipRoles(){
		if (!sumPipRolesBuffer){
			sumPipRolesBuffer = 0;
			for(Role role : getRoles()) {
                if (role.isPJ())
				    sumPipRolesBuffer += role.getPipi() + role.getPipr();
			}
		}
		return sumPipRolesBuffer;
	}

	public boolean hasPlotTag(Tag parPlotTag) {
		for (PlotHasTag plotHasPlotTag : extTags) {
			if (plotHasPlotTag.tag == parPlotTag) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUnivers(Univers parUnivers) {
		for (PlotHasUnivers plotHasUnivers : plotHasUniverses) {
			if (plotHasUnivers.univers == parUnivers) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUniversGeneric() {
		int i = 0;
		for (PlotHasUnivers plotHasUnivers : plotHasUniverses) {
			i++;
		}
		return i == 0;
	}
	
	public int getNbMinMen () {
		int number = 0;
		for (Role role : getRoles()) {
			if (role.isPJ() && role.isMen()) {
				number++;
			}
		}
		return number;
	}
	
	public int getNbMinWomen () {
		int number = 0;
		for (Role role : getRoles()) {
			if (role.isPJ() && role.isWomen()) {
				number++;
			}
		}
		return number;
	}

    Integer getterId() {
        return id;
    }
}
