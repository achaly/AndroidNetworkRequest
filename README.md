# AndroidNetworkRequest
A Simple Synchronous HTTP Wrapper Library for Android. Using HttpUrlConnection. Easy and Flexible.

# Use
StringRequest and JsonRequest can be find in DemoActivity.

Sample:

    new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringRequest request = new StringRequest("https://www.google.com");
                int statusCode = request.getStatus();
                switch (statusCode) {
                    case Request.STATUS_OK: {
                        return request.getResponseResult();
                    }
                    case Request.STATUS_NETWORK_UNAVAILABLE: {
                        return "STATUS_NETWORK_UNAVAILABLE";
                    }
                    case Request.STATUS_NOT_MODIFIED: {
                        return "STATUS_NOT_MODIFIED";
                    }
                    default: {
                        return "STATUS_UNKNOWN";
                    }
                }
            }

            @Override
            protected void onPostExecute(String s) {
                textView2.setText(s);
            }

    }.execute();
