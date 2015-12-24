#This is the main flask application code
from flask import Flask, render_template, request, redirect, url_for
import pymongo
import os
import csv

application = Flask(__name__)

try:
    conn=pymongo.MongoClient(host='ec2-54-200-192-132.us-west-2.compute.amazonaws.com', port=27017)
    print "Connected successfully!!!"
	#db = conn.rtdb
    db=conn.Palette
    #rests= db.rests
    rests=db.restaurant
    #menus=db.menus
    menus=db.menu
    #deals=db.deals
	#posts=db.posts
    print rests.count()
    print menus.count()
except Exception, e:
	print "Failed on MongoDB", str(e)

print "reached here"

@application.route('/')
def index():
    return render_template('user.html')

@application.route('/dashboard')
def dashboard():
    return render_template('dashboard.html')

@application.route('/user')
def user():
    return render_template('user.html')

@application.route('/analytics')
def analytics():
    return render_template('analytics.html')

@application.route('/deals')
def deals():
    return render_template('table.html')




@application.route('/<int:restaurant_id>/')
def restaurantMenu(restaurant_id=1):
    #return "page to create a new menu item. Task 1 complete!"
    #restaurant_id=1
    r=rests.find({'_id':str(restaurant_id)})
    restaurant=r[0]
    items=list(menus.find({'restId':str(restaurant_id)}))
    return render_template('menu1.html', restaurant=restaurant, items=items)
    #r = rests.find({'_id':str(restaurant_id)})
    #restaurant=r[0]['name']
    #items=list(menus.find({'rid':str(restaurant_id)}))
    #return render_template('menu.html', restaurant=restaurant, items=items)

# Task 1: Create route for newMenuItem function here


@application.route('/restaurant/<int:restaurant_id>/newmenuitem/', methods=['GET', 'POST'])
def newMenuItem(restaurant_id=1):
	if request.method=='POST':
		newitem={}
		newitem['name'] = request.form['name']
		newitem['description'] = request.form['description']
		newitem['price'] = request.form['price']
		newitem['course'] = request.form['course']
		newitem['rid'] = str(restaurant_id)
		menus.insert(newitem)
		return redirect(url_for('restaurantMenu', restaurant_id=restaurant_id))
	else:
		return render_template(
            'newmenuitem1.html', restaurant_id=restaurant_id)

@application.route('/restaurant/<int:restaurant_id>/newdeal/', methods=['GET', 'POST'])
def newDeal(restaurant_id=1):
	if request.method=='POST':
		newitem={}
		newitem['name'] = request.form['name']
		newitem['description'] = request.form['description']
		newitem['discount'] = request.form['discount']
		newitem['rid'] = str(restaurant_id)
		deals.insert(newitem)
		return redirect(url_for('restaurantMenu', restaurant_id=restaurant_id))
	else:
		return render_template(
            'newdeal.html', restaurant_id=restaurant_id)

@application.route('/restaurant/<int:restaurant_id>/dishSearch/', methods=['GET', 'POST'])
def dishSearch(restaurant_id=1):
    if request.method=='POST':
        val = request.form['name']
        dishes=menus.find()
        rest_list=[]
        for dish in dishes:
            if val.lower() in str(dish['name']).lower():
                rid=dish['restId']
                r_val=rests.find_one({'_id':str(rid)})
                if r_val in rest_list:
                    continue
                else:
                    rest_list.append(r_val)

        return render_template(
            'dishresults.html', restaurant_id=restaurant_id, rest_list=rest_list)
    else:
        return render_template(
            'dishsearch.html', restaurant_id=restaurant_id)


# Task 2: Create route for editMenuItem function here

@application.route('/restaurants/<int:restaurant_id>/<int:menu_id>/edit',
           methods=['GET', 'POST'])
def editMenuItem(restaurant_id=1, menu_id=1):
    editedItem = menus.find_one({'_id':str(menu_id)})
    if request.method == 'POST':
        if request.form['name']:
            editedItem['name'] = request.form['name']
        if request.form['description']:
            editedItem['description'] = request.form['description']
        if request.form['price']:
            editedItem['price'] = request.form['price']
        if request.form['course']:
            editedItem['course'] = request.form['course']
        menus.update({"_id":editedItem['_id']},{"$set":{"name":editedItem['name'],"price":editedItem['price'],"description":editedItem['description'],"course":editedItem['course']}})
        return redirect(url_for('restaurantMenu', restaurant_id=restaurant_id))
    else:

        return render_template(
            'editmenuitem1.html', restaurant_id=restaurant_id, menu_id=menu_id, item=editedItem)

@application.route('/bookings')
def bookings():
    return render_template('bookings.html')

@application.route('/feedback')
def getFeedback(restaurant_id=1):
    #return "page to create a new menu item. Task 1 complete!"
    #restaurant_id=1
    r=rests.find({'_id':str(restaurant_id)})
    restaurant=r[0]
    items=list(menus.find({'restId':str(restaurant_id)}).limit(5))
    return render_template('menu1.html', restaurant=restaurant, items=items)
    #r = rests.find({'_id':str(restaurant_id)})
    #restaurant=r[0]['name']
    #items=list(menus.find({'rid':str(restaurant_id)}))
    #return render_template('menu.html', restaurant=restaurant, items=items)

# Task 1: Create route for newMenuItem function here



#@application.route('/restaurant/<int:restaurant_id>/<int:menu_id>/edit/')
#def editMenuItem(restaurant_id, menu_id):
    #return "page to edit a menu item. Task 2 complete!"


# Task 3: Create a route for deleteMenuItem function here


# For a given file, return whether it's an allowed type or not
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in application.config['ALLOWED_EXTENSIONS']


   
@application.route('/signup')
def signup():
    return render_template('signup.html')

@application.route('/csvupload')
def csvupload():
    return render_template('index2.html')

@application.route('/formdata',methods=['POST'])
def formdata():
    if request.method== 'POST' :
        d={}
        d['name']=request.form['name']
        d['email']=request.form['email']
        d['url']=request.form['url']
        d['password']=request.form['pass1']
        d['location']=request.form['location']
        d['phone']=request.form['phone']
        d['timings']=request.form['timings']
        d['bio']=request.form['bio']
        posts.insert_one(d)
    # return render_template('csvupload')
    return redirect(url_for('csvupload'))

# Route that will process the file upload
@application.route('/upload', methods=['POST'])
def upload():
    # Get the name of the uploaded file
    file = request.files['file']
    # Check if the file is one of the allowed types/extensions
    if file and allowed_file(file.filename):
        # Make the filename safe, remove unsupported chars
        filename = secure_filename(file.filename)
        if(".csv" in filename):
            reader = csv.reader(open('file.csv', 'r'))
            d = {}
            for row in reader:
                name, desc,price,course,rid = row
                print row
                d={}
                d['name']=name;
                d['description']=desc;
                d['price']=price;
                d['course']=course;
                d['rid']=rid;
                menus.insert_one(d).inserted_id

        # Move the file form the temporal folder to
        # the upload folder we setup
        file.save(os.path.join(application.config['UPLOAD_FOLDER'], filename))
        # Redirect the user to the uploaded_file route, which
        # will basicaly show on the browser the uploaded file
        return redirect(url_for('uploaded_file',
                                filename=filename))

# This route is expecting a parameter containing the name
# of a file. Then it will locate that file on the upload
# directory and show it on the browser, so if the user uploads
# an image, that image is going to be show after the upload
@application.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(application.config['UPLOAD_FOLDER'],
                               filename)



if __name__ == '__main__':
    application.run(threaded=True)
