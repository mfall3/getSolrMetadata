getSolrMetadata
==================

Takes a file containing a list of volume ids, gets metadata for them from HTRC's Solr Proxy API, and writes the metadata to an XMl file.

Usage: java -jar getSolrMetadata.jar inputFile outputFile fieldList endpoint

All arguments are optional, as long as all previous arguments are provided.  For example, if providing outputFile, inputFile must also be provided.

More information about field options and endpoints can be found at https://wiki.htrc.illinois.edu/display/COM/Solr+Proxy+API+User+Guide

## arguments:

+ *inputFile* is the filename of the list of HathiTrust volume ids.  Default is volumeIds.txt in current directory.

+ *outputFile* is the filename to write to.  Default is metadata.xml in current directory.

+ *fieldLIst* is the list of fields to include in the response.  Values should be separated by commas, but no spaces.  The default value means all fields.

+ *endpoint* is the solr proxy endpoint. Default is http://chinkapin.pti.indiana.edu:9994/solr/meta/select/, which does not include full text search.