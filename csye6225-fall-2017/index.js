var aws = require('aws-sdk');
var ses = new aws.SES({
   region: 'us-east-1'
});

exports.handler = function(event, context) {
    console.log("Incoming: ", event);
   // var output = querystring.parse(event);
    var SnsMessage = event.Records[0].Sns.Message;
    var url = event.Records[0].Sns.Subject;

    console.log(event.Records[0].Sns.Message);
    var eParams = {
        Destination: {
            ToAddresses: [JSON.parse(event.Records[0].Sns.Message)] 
        },
        Message: {
            Body: {
                Text: {
                    Data: url
                }
            },
            Subject: {
                Data: “Reset password”
            }
        },
        Source: “password-reset@csye6225-fall2017-jainyo.me"
    };

    console.log('===SENDING EMAIL===');
    var email = ses.sendEmail(eParams, function(err, data){
        if(err) console.log(err);
        else {
            console.log("===EMAIL SENT===");
            console.log(data);


            console.log("EMAIL CODE END");
            console.log('EMAIL: ', email);
            context.succeed(event);

        }
    });
}
