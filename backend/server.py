from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
from flask_excel as excel

# Declarations and initializations. (Database, flask instance, etc...)
engine = create_engine('sqlite:///:memory:', echo=True)
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///gmoofficer.sqlite3'
app.config['SECRET_KEY'] = "random string or whatever you want"
db = SQLAlchemy(app)
excel.init_excel(app)

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
'''
    returns product when upc=upc
    '''
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

app.run(host='0.0.0.0')
