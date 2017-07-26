// {"address": {"address":"101 Adams Lane", "postalCode":"99501"}, "customerId": "10000000-0000-0000-0000-000000000001"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000001').next()
a = g.V().hasLabel('address').has('address', '101 Adams Lane').has('postalCode', '99501').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"102 Bellevue Blvd", "postalCode":"21201"}, "customerId": "10000000-0000-0000-0000-000000000002"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000002').next()
a = g.V().hasLabel('address').has('address', '102 Bellevue Blvd').has('postalCode', '21201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"103 Charity Court", "postalCode":"45201"}, "customerId": "10000000-0000-0000-0000-000000000003"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000003').next()
a = g.V().hasLabel('address').has('address', '103 Charity Court').has('postalCode', '45201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"104 Dilbert Drive", "postalCode":"48201"}, "customerId": "10000000-0000-0000-0000-000000000004"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000004').next()
a = g.V().hasLabel('address').has('address', '104 Dilbert Drive').has('postalCode', '48201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"105 Echo Lane", "postalCode":"79900"}, "customerId": "10000000-0000-0000-0000-000000000005"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000005').next()
a = g.V().hasLabel('address').has('address', '105 Echo Lane').has('postalCode', '79900').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"106 Felicity Lane", "postalCode":"33301"}, "customerId": "10000000-0000-0000-0000-000000000006"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000006').next()
a = g.V().hasLabel('address').has('address', '106 Felicity Lane').has('postalCode', '33301').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"107 George Court", "postalCode":"54301"}, "customerId": "10000000-0000-0000-0000-000000000007"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000007').next()
a = g.V().hasLabel('address').has('address', '107 George Court').has('postalCode', '54301').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"108 Harris Avenue", "postalCode":"96801"}, "customerId": "10000000-0000-0000-0000-000000000008"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000008').next()
a = g.V().hasLabel('address').has('address', '108 Harris Avenue').has('postalCode', '96801').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"109 Iowa Avenue", "postalCode":"46201"}, "customerId": "10000000-0000-0000-0000-000000000009"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000009').next()
a = g.V().hasLabel('address').has('address', '109 Iowa Avenue').has('postalCode', '46201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"110 Joshua Avenue", "postalCode":"65101"}, "customerId": "10000000-0000-0000-0000-000000000010"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000010').next()
a = g.V().hasLabel('address').has('address', '110 Joshua Avenue').has('postalCode', '65101').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"111 Karma Court", "postalCode":"33040"}, "customerId": "10000000-0000-0000-0000-000000000011"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000011').next()
a = g.V().hasLabel('address').has('address', '111 Karma Court').has('postalCode', '33040').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"112 Leming Lane", "postalCode":"70598"}, "customerId": "10000000-0000-0000-0000-000000000012"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000012').next()
a = g.V().hasLabel('address').has('address', '112 Leming Lane').has('postalCode', '70598').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"113 Memphis Parkway", "postalCode":"39301"}, "customerId": "10000000-0000-0000-0000-000000000013"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000013').next()
a = g.V().hasLabel('address').has('address', '113 Memphis Parkway').has('postalCode', '39301').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"102 Bellevue Blvd", "postalCode":"21201"}, "customerId": "10000000-0000-0000-0000-000000000014"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000014').next()
a = g.V().hasLabel('address').has('address', '102 Bellevue Blvd').has('postalCode', '21201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"115 Naples Street", "postalCode":"99641"}, "customerId": "10000000-0000-0000-0000-000000000015"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000015').next()
a = g.V().hasLabel('address').has('address', '115 Naples Street').has('postalCode', '99641').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"116 Ocean Place", "postalCode":"73101"}, "customerId": "10000000-0000-0000-0000-000000000016"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000016').next()
a = g.V().hasLabel('address').has('address', '116 Ocean Place').has('postalCode', '73101').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"117 Pelican Place", "postalCode":"19092"}, "customerId": "10000000-0000-0000-0000-000000000017"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000017').next()
a = g.V().hasLabel('address').has('address', '117 Pelican Place').has('postalCode', '19092').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"118 Quisenberry Avenue", "postalCode":"11011"}, "customerId": "10000000-0000-0000-0000-000000000018"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000018').next()
a = g.V().hasLabel('address').has('address', '118 Quisenberry Avenue').has('postalCode', '11011').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"119 Reginald Road", "postalCode":"88201"}, "customerId": "10000000-0000-0000-0000-000000000019"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000019').next()
a = g.V().hasLabel('address').has('address', '119 Reginald Road').has('postalCode', '88201').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"120 Sap Street", "postalCode":"71104"}, "customerId": "10000000-0000-0000-0000-000000000020"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000020').next()
a = g.V().hasLabel('address').has('address', '120 Sap Street').has('postalCode', '71104').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"121 Tonka Trail", "postalCode":"75799"}, "customerId": "10000000-0000-0000-0000-000000000021"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000021').next()
a = g.V().hasLabel('address').has('address', '121 Tonka Trail').has('postalCode', '75799').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"122 Under Court", "postalCode":"99684"}, "customerId": "10000000-0000-0000-0000-000000000022"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000022').next()
a = g.V().hasLabel('address').has('address', '122 Under Court').has('postalCode', '99684').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"123 Verde Drive", "postalCode":"32960"}, "customerId": "10000000-0000-0000-0000-000000000023"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000023').next()
a = g.V().hasLabel('address').has('address', '123 Verde Drive').has('postalCode', '32960').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"124 Xanadu Boulevard", "postalCode":"45385"}, "customerId": "10000000-0000-0000-0000-000000000024"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000024').next()
a = g.V().hasLabel('address').has('address', '124 Xanadu Boulevard').has('postalCode', '45385').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"125 Wiggins Way", "postalCode":"82732"}, "customerId": "10000000-0000-0000-0000-000000000025"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000025').next()
a = g.V().hasLabel('address').has('address', '125 Wiggins Way').has('postalCode', '82732').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"125 Yeti Circle", "postalCode":"10701"}, "customerId": "10000000-0000-0000-0000-000000000026"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000026').next()
a = g.V().hasLabel('address').has('address', '125 Yeti Circle').has('postalCode', '10701').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"1284 Zzyzx Road", "postalCode":"92309"}, "customerId": "10000000-0000-0000-0000-000000000027"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000027').next()
a = g.V().hasLabel('address').has('address', '1284 Zzyzx Road').has('postalCode', '92309').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"1265 Anchor Terrace SW", "postalCode":"30311"}, "customerId": "10000000-0000-0000-0000-000000000028"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000028').next()
a = g.V().hasLabel('address').has('address', '1265 Anchor Terrace SW').has('postalCode', '30311').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"739 Baylor Ave", "postalCode":"91902"}, "customerId": "10000000-0000-0000-0000-000000000029"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000029').next()
a = g.V().hasLabel('address').has('address', '739 Baylor Ave').has('postalCode', '91902').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"4619 Crimson Cir N", "postalCode":"80917"}, "customerId": "10000000-0000-0000-0000-000000000030"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000030').next()
a = g.V().hasLabel('address').has('address', '4619 Crimson Cir N').has('postalCode', '80917').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"1817 S Barrington Ave", "postalCode":"90025"}, "customerId": "10000000-0000-0000-0000-000000000031"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000031').next()
a = g.V().hasLabel('address').has('address', '1817 S Barrington Ave').has('postalCode', '90025').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"3440 Topping Rd", "postalCode":"53705"}, "customerId": "10000000-0000-0000-0000-000000000032"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000032').next()
a = g.V().hasLabel('address').has('address', '3440 Topping Rd').has('postalCode', '53705').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"650 Del Prado Drive", "postalCode":"89005"}, "customerId": "10000000-0000-0000-0000-000000000033"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000033').next()
a = g.V().hasLabel('address').has('address', '650 Del Prado Drive').has('postalCode', '89005').next()
c.addEdge('hasAddress', a)

// {"address": {"address":"650 Del Prado Drive", "postalCode":"89005"}, "customerId": "10000000-0000-0000-0000-000000000034"}
c = g.V().hasLabel('customer').has('customerId', '10000000-0000-0000-0000-000000000034').next()
a = g.V().hasLabel('address').has('address', '650 Del Prado Drive').has('postalCode', '89005').next()
c.addEdge('hasAddress', a)