from flask import Flask, jsonify
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine

# Declarations and initializations. (Database, flask instance, etc...)
engine = create_engine('sqlite:///:memory:', echo=True)
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///gmoofficer.sqlite3'
app.config['SECRET_KEY'] = "random string or whatever you want"
db = SQLAlchemy(app)


'''
HW #1: Define the equivelent structure for 'Product' model.
Consider: What data do you have access to, and what data does the user
or app need to know...?

Ex: would a 'brand' field be useful? Is it available?
*** Consider doing #5 before any of the rest **
'''


class Product(db.Model):
    id = db.Column('products_id', db.Integer, primary_key = True)
    name = db.Column(db.String(100))
    foodtype = db.Column(db.Enum('soup', 'cereal', 'Mints'))
    price = db.Column(db.String(200))
    upc = db.Column(db.String(100))

    # '__init__' gets called when you instantiate this class. Typically
    # it is called a constructor.
    # Notice the default arguments.
    '''
    HW #2: What would be a better default argument for 'price'
    Extra-credit, what would be a better default argument for the
    Enum typed variable 'foodtype'?
     '''
    def __init__(self, name="", foodtype=0, price=0.0, upc=""):
        self.name=name
        self.foodtype=foodtype
        self.price=price
        self.upc=upc

    @property
    def serialize(self):
        '''Return object data in easily serializeable format'''
        return {
            'id': self.id,
            'name':self.name,
            'upc': self.upc,
            'price': self.price,
            'foodtype': self.foodtype
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
    HW #3: Figure out how to return the Product whose id=product_id from
    the url above. A query that finds all of them is below. (hind: 'filter')

    Use the serialize_list function I wrote to turn the object lists (only lists)
    into valid json. If you have a single item do not use it.
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
HW #4: Add another route that will return all of them at the url:

    "/api/products"

Use the one above as template.
'''

def add_product(name, foodtype, price, upc):
    product = Product(name, foodtype, price, upc)
    db.session.add(product)
    db.session.commit()
    return product

db.create_all()

''' HW #5: Create some example products and save them to the database '''
add_product("Newman's Own Pesto Ravioli", "soup", 4.99, "11111")
add_product("Fritos honey BBQ", "cereal", 1.99, "11111")
add_product("Tic Tac Wintergreen", "Mints", .99, "009800007677")
#add_product("Fritos honey BBQ", "Cereal", 1.99)  # WONT WORK

app.run(host='0.0.0.0')
