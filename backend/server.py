#!/usr/bin/env python
# -*- coding: utf-8 -*-

from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
from flask.ext import excel # this was what I changed
import datetime
import bcrypt
import jwt

# Declarations and initializations. (Database, flask instance, etc...)
engine = create_engine('sqlite:///:memory:', echo=True)
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///gmoofficer.sqlite3'
app.config['SECRET_KEY'] = "random string or whatever you want"
db = SQLAlchemy(app)
excel.init_excel(app)

secret_key="secret key"


class User(db.Model):
    """ User Model for storing user related details """
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    email = db.Column(db.String(255), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    registered_on = db.Column(db.DateTime, nullable=False)
    admin = db.Column(db.Boolean, nullable=False, default=False)

    def __init__(self, email, password, admin=False):
        salt1 = b"$2a$12$w40nlebw3XyoZ5Cqke14M."
        self.email = email
        self.password = bcrypt.hashpw(
            password, salt1
        ).decode()
        self.registered_on = datetime.datetime.now()
        self.admin = admin

    @staticmethod
    def decode_auth_token(auth_token):
        try:
            payload = jwt.decode(auth_token, secret_key)
            print payload
            return payload['sub']
        except jwt.ExpiredSignatureError:
            return 'Signature expired. Please log in again.'
        except jwt.InvalidTokenError:
            return 'Invalid token. Please log in again.'

    def encode_auth_token(self):
        try:
            payload = {
                'exp': datetime.datetime.utcnow() + datetime.timedelta(days=0, seconds=5),
                'iat': datetime.datetime.utcnow(),
                'sub': self.id
            }
            return jwt.encode(
                payload,
                secret_key,
                algorithm='HS256' 
                )
        except Exception as e:
            return e
        return None

    @property
    def serialize(self):
        '''Return object data in easily serializeable format'''
        return {
            'id': self.id,
            'email':self.email,
            'registered_on': self.registered_on,
        }

'''
def of product class
'''
class Product(db.Model):
    id = db.Column('products_id', db.Integer, primary_key = True)
    name = db.Column(db.String(100))
    foodtype = db.Column(db.Enum('soup', 'cereal', 'Mints'))
    price = db.Column(db.String(200))
    upc = db.Column(db.String(100))
    isgmo = db.Column(db.Boolean())

    # '__init__' gets called when you instantiate this class. Typically
    # it is called a constructor.
    # Notice the default arguments.
    '''
    default arguments
     '''
    def __init__(self, name="", foodtype=0, price=0.0, upc="", isgmo=False):
        self.name=name
        self.foodtype=foodtype
        self.price=price
        self.upc=upc
        self.isgmo=isgmo

    @property
    def serialize(self):
        '''Return object data in easily serializeable format'''
        return {
            'id': self.id,
            'name':self.name,
            'upc': self.upc,
            'price': self.price,
            'foodtype': self.foodtype,
            'isgmo': self.isgmo
        }


@app.route('/')
def hello_world():
    return 'Hello, Welcome to gmoofficer!'


# this captures the value at a certain location in the url
# ex. 127.0.0.1:5000/api/products/1
# would return the string: "Number: 1"
@app.route('/api/products/<string:upc>')
def getProduct(upc):
    print upc # will print the number at the end of the url

#returns product when upc=upc

    p = Product.query.filter_by(upc=upc).first()
#   p = Product.query.all() # this returns ALL of them, change this

    # for debugging...
    print "\n\nDEBUG:\n\nQuery objects: ", p

    # serializes the list of items
    #item_list=serialize_list(p)

    # debugging....
    #print "Serialized items: ", item_list, "\n\n"
    return jsonify(p.serialize)


def serialize_list(in_list):
    return [a.serialize for a in in_list]


@app.route('/api/products')
def show_all():
    item_list = serialize_list(Product.query.all())
    print item_list
    return jsonify(item_list)

'''
returns all of them at the url /api/products
'''


def add_product(name, foodtype, price, upc,isgmo):
    product = Product(name, foodtype, price, upc,isgmo)
    db.session.add(product)
    db.session.commit()
    return product

db.create_all()

''' example products'''
add_product("Newman's Own Pesto Ravioli", "soup", 4.99, "11111",False)
add_product("Fritos honey BBQ", "cereal", 1.99, "11111",False)
add_product("Tic Tac Wintergreen", "Mints", .99, "009800007677",True)
#add_product("Fritos honey BBQ", "Cereal", 1.99)  # WONT WORK

'''
test code for uploading excel files using flask_excel,
eventually for uploading the non-gmo spreadhsheet
'''

@app.route("/upload", methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        excel_input = request.get_array(field_name='file')
        output = []
        for row in excel_input[1:]: # slice
            p = Product(*row[1:])
            db.session.add(p)
            db.session.commit()
            output.append(p)

        return jsonify({"result": serialize_list(output)})

    return '''
    <!doctype html>
    <title>Upload an excel file</title>
    <h1>Excel file upload (csv, tsv, csvz, tsvz only)</h1>
    <form action="" method=post enctype=multipart/form-data><p>
    <input type=file name=file><input type=submit value=Upload>
    </form>
    '''


@app.route("/download", methods=['GET'])
def download_file():
    return excel.make_response_from_array([[1, 2], [3, 4]], "csv")


@app.route("/export", methods=['GET'])
def export_records():
    return excel.make_response_from_array([["ID", "Name", "Food Type", "Price", "UPC", "isGMO"], [1, "Tic Tacs RED", "Mints", 19.99, "009800007677", False],
                                            [1, "Tic Tacs BLUE", "Mints", 19.99, "009800007677", False], [1, "Tic Tacs PINK", "Mints", 19.99, "009800007677", False]], "csv",
                                          file_name="export_data")


@app.route("/download_file_named_in_unicode", methods=['GET'])
def download_file_named_in_unicode():
    return excel.make_response_from_array([["one", "two"], ["three", 4]], "csv",
                                          file_name="download_file")



@app.route("/user", methods=['POST'])
def create_user():
    return
    

@app.route("/api/users/", methods=['GET'])
def list_users():
    users = User.query.all()
    return jsonify({"result": serialize_list(users),
                    "token": users[0].encode_auth_token()})

if __name__ == "__main__":
    excel.init_excel(app)


    u = User("bryan.mccoid@gmail.com", "somepassword", admin=True)
    db.session.add(u)
    db.session.commit()
    auth_token = u.encode_auth_token()
    print auth_token
    decoded = u.decode_auth_token(auth_token)
    print decoded


app.run(host='0.0.0.0')
