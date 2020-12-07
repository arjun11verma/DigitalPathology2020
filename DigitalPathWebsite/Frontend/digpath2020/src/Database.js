import * as Realm from "realm-web";

const mongo_app = new Realm.App({id: "digitalpathology2020-ecrjr", timeout: 10000});

export const app = mongo_app;