
from flask import Flask, redirect, url_for, session, request,render_template
from flask_oauth import OAuth
import json
import pymongo
try:
    conn = pymongo.MongoClient(host='ec2-54-200-192-132.us-west-2.compute.amazonaws.com', port=27017)
    print "Connected successfully!!!"
    db = conn['Palette']
    users=db.users
    restaurant=db.restaurant
    menu=db.menu
    tables=db.tables
    torate=db.torate
    ratings=db.ratings
    tables=db.tables
except Exception, e:
    print "Failed on MongoDB", str(e)

SECRET_KEY = 'Cloud Project'
DEBUG = True
FACEBOOK_APP_ID = ''
FACEBOOK_APP_SECRET = ''


application = Flask(__name__)
application.debug = DEBUG
application.secret_key = SECRET_KEY
oauth = OAuth()

facebook = oauth.remote_app('facebook',
    base_url='https://graph.facebook.com/',
    request_token_url=None,
    access_token_url='/oauth/access_token',
    authorize_url='https://www.facebook.com/dialog/oauth',
    consumer_key=FACEBOOK_APP_ID,
    consumer_secret=FACEBOOK_APP_SECRET,
    request_token_params={'scope': 'email'}
)


@application.route('/')
def index():
    return render_template('fb.html')

@application.route('/login')
def login():
    return facebook.authorize(callback=url_for('facebook_authorized',
        next=request.args.get('next') or request.referrer or None,
        _external=True))

@application.route('/signup')
def signup():
    return render_template('signup.html')

@application.route('/signupdetails', methods=['POST'])

def signupdetails():
    if request.method== 'POST'  :
        d={}
        d['name']=session['name']
        d['id']=session['id']
        d['email']=request.form['email']
        d['address']=request.form['address']
        d['city']=request.form['city']
        d['country']=request.form['country']
        d['phone']=request.form['phone']
        d['zip']=request.form['zip']
        d['preferences']=request.form['preferences']
        d['ratings']={}
        d['rated']={}
        users.insert_one(d)
    return redirect('home')

@application.route('/home',methods=['GET','POST'])
def home(): 
    val=users.find_one({"id":session['id']})
    res=db.restaurant.find({"cuisine": {"$regex": val['preferences'] }}).sort("stars",pymongo.DESCENDING).limit(10)
    li=[]
    for var in res:
        d={}
        d['name']=var['name']
        d['cuisine']=var['cuisine']
        d['stars']=var['stars']
        d['id']=var['_id']
        li.append(d)
    session['homeData']=li
    print li
    return render_template('homepage.html')
@application.route('/homedetails',methods=['GET','POST'])
def homedetails():
    if request.method == 'POST':
        val=request['val']
    return val
@application.route('/chooseClass/<int:restaurant_id>',methods=['GET','POST'])
def chooseClass(restaurant_id):
    session['restId']=restaurant_id
    return redirect(url_for('restaurant'))

@application.route('/restaurant')
def restaurant():
    res=db.restaurant.find_one({"_id":str(session['restId'])})
    session['restName']=res['name']
    session['cuisine']=res['cuisine']
    session['phone']=res['phone']
    session['address']=res['address']
    session['hours']=res['hours']
    session['stars']=res['stars']
    sC=res['sentimentCount']
    sS=res['sentimentSum']
    rS=res['ratingSum']
    rC=res['ratingCount']
    # if(sC==0):
    #     sC=1
    #     sS=3
    # if(rC==0):
    #     rC=1
    #     rS=3
    if(int(rC)>=1):
        session['rating']=float(rS)/int(rC)
    else:
        session['rating']=-1
    if(int(sC)>=1):
        session['sentiment']=float(sS)/int(sC)
    else:
        session['sentiment']=-1
    return render_template('restaurantpage.html')

@application.route('/bookdetails', methods=['POST'])
def bookdetails():
    if request.method == 'POST':
        radio=request.form['radio']
        res=db.restaurant.find_one({"_id":str(session['restId'])},{"timings":1,"_id":0})
        exp=res['timings']
        exp[radio]=1

        result = db.restaurant.update_one(
        {"_id": '1'},
        {
            "$set": {
            "timings": exp
            }
        }
        )
        session['radio']=radio
        return render_template('confirmbook.html')
# @application.route('/homepage')
# def homepage():
#     return

@application.route('/book', methods=['GET','POST'])
def book():
    li=[]
    res=db.restaurant.find_one({"_id":str(session['restId'])},{"timings":1,"_id":0})
    exp=(res['timings'])
    print exp
    for i in exp:
        if(exp[i]==0):
            li.append(i)
    session['timings']=li
    return render_template('booktable.html')

@application.route('/rating')
def rating():
    res=torate.find({"userId":session['id']})
    lis=[]
    for val in res:
        d={}
        d["dishId"]=val['dishId']
        d["dishName"]=val['dishName']
        d["restId"]=val['restId']
        d["restName"]=val['restName']
        lis.append(d)
    session['rating']=lis
    return render_template('rating.html')

@application.route('/submitRatings', methods=['GET','POST'])
def submitRatings():
    if request.method == 'POST':
        for i in session['rating']:
            val=request.form[i['dishId']]
            ratings.insert_one({"userId":session['id'],"dishId":i['dishId'],"dishName":i['dishName'],"restId":i['restId'],"restName":i['restName'],"rating":val})
            torate.remove({"userId":session['id']})
    return redirect(url_for('home'))

@application.route('/dishSearch/<int:restaurant_id>/dishSearch/', methods=['GET', 'POST'])
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


@application.route('/login/authorized')
@facebook.authorized_handler
def facebook_authorized(resp):
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['oauth_token'] = (resp['access_token'], '')
    me = facebook.get('/me')
    session['name']=me.data['name']
    session['id']=me.data['id']
    
    if(users.find({"name":me.data['name']}).count()>0):
        return redirect(url_for('home'))
    else:
        return redirect(url_for('signup'))
    # return 'Logged in as id=%s name=%s  redirect=%s '% \
    #     (me.data['id'], me.data['name'],request.args.get('next'))


@facebook.tokengetter
def get_facebook_oauth_token():
    return session.get('oauth_token')


if __name__ == '__main__':
    application.run()

