const Realm = require("realm-web");

const appId = "digitalpathology2020-ecrjr";
const graphQLUri = `https://realm.mongodb.com/api/client/v2.0/app/${appId}/graphql`;

const mongo_app = new Realm.App({id: appId, timeout: 10000});

const checkIn = () => {
    return mongo_app.currentUser.isLoggedIn;
}

const login = async(email, password) => {
    const user = await app.logIn(Realm.Credentials.emailPassword(email, password));
    return user.id === app.currentUser.id;
}

const getAccessToken = async() => {
    await mongo_app.currentUser.refreshAccessToken();
    return mongo_app.currentUser.accessToken;
}

export const app = mongo_app;
export const check = checkIn;
export const logIn = login;
export const accessToken = getAccessToken;
export const accessUri = graphQLUri;