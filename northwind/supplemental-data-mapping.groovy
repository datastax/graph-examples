config create_schema: false, load_new: false

// If the user specifies an inputpath on the command-line, use that.
// Otherwise check the data directory from the data directory from where the loader is run.
if (inputpath == '')
    path = new java.io.File('.').getCanonicalPath() + '/data/'
else
    path = inputpath + '/'

def fbMembersInput = File.csv(path + 'facebook_members.csv').delimiter('|')
def identitiesInput = File.csv(path + 'identity_c2fb.csv').delimiter('|')
def isFriendsWithInput = File.csv(path + 'isFriendsWith.csv').delimiter('|')
def isRelatedToInput = File.csv(path + 'isRelatedTo.csv').delimiter('|')
def ratedInput = File.csv(path + 'rated.csv').delimiter('|')

//Specifies what data source to load using which mapper
load(fbMembersInput).asVertices {
    label "networkMember"
    key "id"
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
        key "id"
    }
    inV "nameTo", {
        label "networkMember"
        key "id"
    }
}

load(isRelatedToInput).asEdges {
    label "isRelatedTo"
    outV "nameFrom", {
        label "networkMember"
        key "id"
    }
    inV "nameTo", {
        label "networkMember"
        key "id"
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

