from pymongo import MongoClient, DESCENDING
import pymongo
client = MongoClient()
db=client['Palette']
users=db.users
menu=db.menu
tables=db.tables
# res=users.update_one({},{"$set":{"preferences":"dal"}})
# print res.matched_count
# res=users.find_one({},{"name":1,"preferences":1,"_id":0})
# print res['preferences']
# d={}
# d['id']=3
# d['restaurant name']="masala club"
# d['dish']="dal"
# d['review']="it was good"
# d['sentiment']=5
# menu.insert_one(d)

# d={}
# d['restName']="Doaba Deli"
# d['restId']=1
# d['timings']={'12:00-AM':0,'01:00-AM':0,'02:00-AM':0,'03:00-AM':0,'04:00-AM':0,'05:00-AM':0,'06:00-AM':0,'07:00-AM':0,'08:00-AM':0,'09:00-AM':0}
# tables.insert_one(d)


# d={}
# d['id']=2
# d['restaurant name']='pizza hut'
# d['dish']="pizza"
# d['review']="bad"
# d['sentiment']=0
# menu.insert_one(d)

# res=menu.find({"dish":/va/},{"_id":0})
res=db.tables.find_one({"restId":1})
di=res['timings']
print di['09:00-AM']
for i in di:
	print i
	if(di[i]==0):
		print di[i]
# for key, value in di.iteritems()
# 	print key, value
# res=db.menu.find({"dish": {'$regex': 'da'}})
# for val in res:
# 	print val['restaurant name']
