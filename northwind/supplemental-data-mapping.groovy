config create_schema: true, load_new: false

def inputpath = '/advanced-graph-training/northwind/data/';

def fbMembersInput = File.csv(inputpath + 'facebook_members.csv').delimiter('|')
def identitiesInput = File.csv(inputpath + 'identity_c2fb.csv').delimiter('|')
def isFriendsWithInput = File.csv(inputpath + 'isFriendsWith.csv').delimiter('|')
def isRelatedToInput = File.csv(inputpath + 'isRelatedTo.csv').delimiter('|')
def ratedInput = File.csv(inputpath + 'rated.csv').delimiter('|')

//Specifies what data source to load using which mapper
load(fbMembersInput).asVertices {
    label "networkMember"
    key "name"
}

load(identitiesInput).asEdges {
    label 'isMember'
    outV 'name', {
        label 'customer'
        key 'name'
    }
    inV 'name', {
        label 'networkMember'
        key 'name'
    }
}

load(isFriendsWithInput).asEdges {
    label "isFriendsWith"
    outV "nameFrom", {
        label "networkMember"
        key "name"
    }
    inV "nameTo", {
        label "networkMember"
        key "name"
    }
}

load(isRelatedToInput).asEdges {
    label "isRelatedTo"
    outV "nameFrom", {
        label "networkMember"
        key "name"
    }
    inV "nameTo", {
        label "networkMember"
        key "name"
    }
}

load(ratedInput).asEdges {
    label "rated"
    outV "customerName", {
        label "customer"
        key "name"
    }
    inV "productId", {
        label "product"
        key "id"
    }

}

