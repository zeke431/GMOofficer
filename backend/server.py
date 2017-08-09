from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine

engine = create_engine('sqlite:///:memory:', echo=True)
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///gmoofficer.sqlite3'

db = SQLAlchemy(app)
class products(db.Model):
   id = db.Column('products_id', db.Integer, primary_key = True)
   name = db.Column(db.String(100))
   foodtype = db.Column(db.String(50))
   price = db.Column(db.String(200))

@app.route('/')
def hello_world():
    return 'Hello, Welcome to gmoofficer!'

def __init__(self, name, foodtype, price):
   self.name = name
   self.foodtype = foodtype
   self.price = price

db.create_all()
