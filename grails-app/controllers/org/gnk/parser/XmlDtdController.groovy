package org.gnk.parser

import org.hibernate.annotations.ForceDiscriminator;

class XmlDtdController {

	static def CAR_RECORDS = '''
    <records>
      <car name='HSV Maloo' make='Holden' year='2006'>
        <country>Australia</country>
        <record type='speed'>Production Pickup Truck with speed of 271kph</record>
      </car>
      <car name='P50' make='Peel' year='1962'>
        <country>Isle of Man</country>
        <record type='size'>Smallest Street-Legal Car at 99cm wide and 59 kg in weight</record>
      </car>
      <car name='Royale' make='Bugatti' year='1931'>
        <country>France</country>
        <record type='price'>Most Valuable Car at $15 million</record>
      </car>
    </records>
	'''
 
	def index() {
		def records = new XmlParser().parseText(CAR_RECORDS)
		def nbRecords = records.car.size()
		
		for (int i= 0; i < nbRecords; ++i)
		{
			def record = records.car[i]
		}
	}
	
	
	
		
}
