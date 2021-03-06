
package org.gnk.naming

import com.gnk.substitution.Tag
import org.gnk.naming.Firstname
import org.gnk.naming.NameAndWeight

import java.util.LinkedList
import java.util.LinkedList

class NamingService 
{
	// Nombre de prenom ou de nom a renvoyer
	Integer selectionNumber = 3
	// Pour �viter de renvoyer toujours le meme prenom en choix 1
	LinkedList<String> usedFirstName = new LinkedList<String>()
	// Pour �viter de renvoyer toujours le meme nom en choix 1
	LinkedList<String> usedName = new LinkedList<String>()
	
	/* Methode principale naming */
	LinkedList<PersoForNaming> namingMethod(LinkedList<PersoForNaming> persoList)
	{
		LinkedList<PersoForNaming> doneperso = new LinkedList<PersoForNaming>()
		// pour les tests de naming
		usedFirstName = new LinkedList<String>()
		usedName = new LinkedList<String>()

        //Univers
        LinkedList linguistique = getTagForUniverse (persoList.first.universe);

        // liste des prenoms possibles
        LinkedList<Firstname> fnlistHomme = getFirstNamebyGender ("m")
        LinkedList<Firstname> fnlistFemme = getFirstNamebyGender ("f")
        Collections.shuffle(fnlistHomme)
        Collections.shuffle(fnlistFemme)

        //liste des noms possibles
        LinkedList<Name> nlist = getNamebyTag(persoList, linguistique)
        Collections.shuffle (nlist)

        // Pour chaque personnage envoye
		for (PersoForNaming tmp : persoList)
		{
            // rajoute les tags linguistique lié à l'univers
            tmp.tag.addAll(linguistique)

			// Choix des prenoms
			if (tmp.is_selectedFirstName)
			{
				NameAndWeight maxname

                LinkedList<Firstname> fnlist = tmp.getgender() == "M" ? fnlistHomme : fnlistFemme

				// tri sur les tags du personnage
				LinkedList<NameAndWeight> fnweight = WeightFNcalcul (tmp, fnlist)

				if (fnweight.empty)
					fnweight = getRandomFirstname(fnlist)

				// ranger la liste dans l'ordre et la mettre dans le perso a renvoyer
				while (fnweight.size() > 0)
				{
					maxname = fnweight.first
					for (NameAndWeight nw : fnweight)
					{
						if (maxname.weight < nw.weight)
							maxname = nw
					}
					if (!usedFirstName.contains(maxname.name))
						tmp.selectedFirstnames.add(maxname.name)
					fnweight.remove(maxname)
					
					if (tmp.selectedFirstnames.size() >= selectionNumber)
						break
				}

				if (tmp.selectedFirstnames.first())
					usedFirstName.add(tmp.selectedFirstnames.first())
			}
			
 			// Si la personne est dans la famille d'un des personnages deja traite, on reprend les memes noms  
			for (PersoForNaming pers : doneperso)
			{
				if (tmp.family.contains(pers.code))
				{
					tmp.selectedNames = pers.selectedNames
				}
			}
			
			// Choix du nom de famille
			if (tmp.is_selectedName && tmp.selectedNames.isEmpty())
			{
				NameAndWeight maxname
				// tri sur les tags du personnage
				LinkedList<NameAndWeight> nweight = WeightNcalcul (tmp, nlist)

				if (nweight.size() < persoList.size())
					nweight += getRandomName(nlist, persoList)

				// verifie que les tags sont bien valides
				if (!nweight.empty)
				{
					// ranger la liste dans l'ordre et la mettre dans le perso a renvoyer
					while (nweight.size() > 0)
					{
						maxname = nweight.first
						for (NameAndWeight nw : nweight)
						{
							if (maxname.weight < nw.weight)
								maxname = nw
						}
                        if (tmp.bannedNames.contains(maxname.name))
                            continue
						if (!usedName.contains(maxname.name))
							tmp.selectedNames.add(maxname.name)
						nweight.remove(maxname)
						
						if (tmp.selectedNames.size() >= selectionNumber)
							break
					}
					if (tmp.selectedNames.first())
						usedName.add(tmp.selectedNames.first())
				}
			}
			// ajout du personnage a la liste des personnages deja traite pour pouvoir retrouver les noms de famille
			doneperso.add(tmp)
		}
		return doneperso
	}

	
	/* Methode en prive */
	// recuperer les prenoms de la base de donnees en fonction du genre (+ les neutre)
	private LinkedList<Firstname> getFirstNamebyGender (String gender)
	{
		LinkedList<Firstname> fnlist
        LinkedList<Firstname> fnlistNeutre

        fnlist = org.gnk.naming.Firstname.createCriteria().list {
            or{
                eq ('gender', gender)
                eq ('gender', 'n')
            }
		}
		return fnlist
	}
	
	// recuperer les noms de la base de donnees en fonction du tag
	private LinkedList<Name> getNamebyTag (LinkedList<PersoForNaming> persoList, LinkedList linguistique)
	{
		LinkedList<Name> nlist = new LinkedList<Name>()
        LinkedList<NameHasTag> ntaglist = new LinkedList<NameHasTag>()

        if (!linguistique.isEmpty())
		{
            for (Tag ltag : linguistique)
            {
                // recuperer le tag differenciant version BDD
                org.gnk.tag.Tag tag = org.gnk.tag.Tag.createCriteria().get {
                    eq ('name', ltag.value)
                }

                LinkedList<NameHasTag> ntaglistsub = org.gnk.naming.NameHasTag.createCriteria().list {
                    eq ('tag', tag)
                }
                ntaglist += ntaglistsub
            }
            if (!ntaglist.isEmpty()) {
                    for (NameHasTag ntag : ntaglist)
                        nlist.add(ntag.name)
                }
            else
                nlist = org.gnk.naming.Name.all
        }
		else
			nlist = org.gnk.naming.Name.all

        if(nlist.size() < (persoList.size() * 3))
        {
            return (org.gnk.naming.Name.all)
        }

		return nlist
	}
	
	
	//calcule la correspondance d'un tag avec le caractere pour les prenoms
	private LinkedList<NameAndWeight> WeightFNcalcul (PersoForNaming tmp, LinkedList fnlist)
	{
		LinkedList<NameAndWeight> fnweight = new LinkedList<NameAndWeight> ()

		for (Firstname fn : fnlist)
        {
            if (tmp.bannedFirstnames.contains(fn.name))
                continue
			NameAndWeight firstn = new NameAndWeight(fn.name, 0)
			for (FirstnameHasTag fntag : fn.extTags)
			{
				for (Tag persotag : tmp.tag )
				{
					// Verifie la valeur du tag
					if (fntag.tag.name.equals(persotag.value))
						firstn.weight += (fntag.weight * persotag.weight)
				}
			}
			
			// Si les poids ont matche, on ajoute a la liste retournee
			if (firstn.weight > 0)
				fnweight.add(firstn)
		}
			
		return fnweight
	}
	
	//calcule la correspondance d'un tag avec le caractere pour les noms
	private LinkedList<NameAndWeight> WeightNcalcul (PersoForNaming tmp, LinkedList nlist)
	{
		LinkedList<NameAndWeight> nweight = new LinkedList<NameAndWeight> ()
		for (Name n : nlist)
		{
			NameAndWeight name = new NameAndWeight(n.name, 0)
			for (NameHasTag ntag : n.extTags)
			{
				for (Tag persotag : tmp.tag )
				{
					// Verifie la valeur du tag
					if ( ntag.tag.name.equals(persotag.value))
                    {
						name.weight += (ntag.weight * persotag.weight)
                    }
				}
			}
			
			// Si les poids ont matche, on ajoute a la liste retournee
			if (name.weight > 0)
                nweight.add(name)
		}
		
		return nweight
	}

	private LinkedList<NameAndWeight> getRandomFirstname (LinkedList fnlist)
	{
		LinkedList<NameAndWeight> fnweight = new LinkedList<NameAndWeight> ()
		for (Firstname fn : fnlist)
		{
			NameAndWeight firstn = new NameAndWeight(fn.name, 0)
			
			fnweight.add(firstn)
		}
		
		return fnweight
	}
		
	private LinkedList<NameAndWeight> getRandomName (LinkedList nlist, LinkedList persoList)
	{
		LinkedList<NameAndWeight> nweight = new LinkedList<NameAndWeight> ()
		for (Name n : nlist)
		{
			NameAndWeight firstn = new NameAndWeight(n.name, 0)
			
			nweight.add(firstn)
            if (nweight.size() > persoList.size())
                break;
		}
		
		return nweight
	}

    // Fait la liste des groupes linguistiques liés à l'univers
    private LinkedList<Tag> getTagForUniverse (String universe)
    {
        LinkedList<Tag> linguistique = new LinkedList<>()

        //faire la liste dans Tagrelation avec id1 = id du tag
        LinkedList<org.gnk.tag.TagRelation> tagrelations = org.gnk.tag.TagRelation.createCriteria().list {
            eq ('tag1', org.gnk.tag.Tag.createCriteria().get {
                eq ('name', universe)
                })
        }
        //faire des tags à partir de tous les tag2 des tagRelations
        for (org.gnk.tag.TagRelation tagrelation : tagrelations)
        {
            com.gnk.substitution.Tag tag = new com.gnk.substitution.Tag(tagrelation.tag2.name,
                                                                        "langue", tagrelation.weight, "")
            if (tag.weight > 0)
                linguistique.add(tag)
        }

        return linguistique
    }

}
