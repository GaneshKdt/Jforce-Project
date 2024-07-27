
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcessShipmentRequest_QNAME = new QName("http://fedex.com/ws/ship/v17", "ProcessShipmentRequest");
    private final static QName _DeleteTagRequest_QNAME = new QName("http://fedex.com/ws/ship/v17", "DeleteTagRequest");
    private final static QName _ProcessTagRequest_QNAME = new QName("http://fedex.com/ws/ship/v17", "ProcessTagRequest");
    private final static QName _ProcessTagReply_QNAME = new QName("http://fedex.com/ws/ship/v17", "ProcessTagReply");
    private final static QName _ShipmentReply_QNAME = new QName("http://fedex.com/ws/ship/v17", "ShipmentReply");
    private final static QName _DeleteShipmentRequest_QNAME = new QName("http://fedex.com/ws/ship/v17", "DeleteShipmentRequest");
    private final static QName _ValidateShipmentRequest_QNAME = new QName("http://fedex.com/ws/ship/v17", "ValidateShipmentRequest");
    private final static QName _ProcessShipmentReply_QNAME = new QName("http://fedex.com/ws/ship/v17", "ProcessShipmentReply");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.nmims.webservice.fedex.ship
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessShipmentReply }
     * 
     */
    public ProcessShipmentReply createProcessShipmentReply() {
        return new ProcessShipmentReply();
    }

    /**
     * Create an instance of {@link ValidateShipmentRequest }
     * 
     */
    public ValidateShipmentRequest createValidateShipmentRequest() {
        return new ValidateShipmentRequest();
    }

    /**
     * Create an instance of {@link DeleteShipmentRequest }
     * 
     */
    public DeleteShipmentRequest createDeleteShipmentRequest() {
        return new DeleteShipmentRequest();
    }

    /**
     * Create an instance of {@link ShipmentReply }
     * 
     */
    public ShipmentReply createShipmentReply() {
        return new ShipmentReply();
    }

    /**
     * Create an instance of {@link ProcessTagReply }
     * 
     */
    public ProcessTagReply createProcessTagReply() {
        return new ProcessTagReply();
    }

    /**
     * Create an instance of {@link ProcessTagRequest }
     * 
     */
    public ProcessTagRequest createProcessTagRequest() {
        return new ProcessTagRequest();
    }

    /**
     * Create an instance of {@link DeleteTagRequest }
     * 
     */
    public DeleteTagRequest createDeleteTagRequest() {
        return new DeleteTagRequest();
    }

    /**
     * Create an instance of {@link ProcessShipmentRequest }
     * 
     */
    public ProcessShipmentRequest createProcessShipmentRequest() {
        return new ProcessShipmentRequest();
    }

    /**
     * Create an instance of {@link DestinationControlDetail }
     * 
     */
    public DestinationControlDetail createDestinationControlDetail() {
        return new DestinationControlDetail();
    }

    /**
     * Create an instance of {@link NotificationParameter }
     * 
     */
    public NotificationParameter createNotificationParameter() {
        return new NotificationParameter();
    }

    /**
     * Create an instance of {@link CustomLabelDetail }
     * 
     */
    public CustomLabelDetail createCustomLabelDetail() {
        return new CustomLabelDetail();
    }

    /**
     * Create an instance of {@link PackageBarcodes }
     * 
     */
    public PackageBarcodes createPackageBarcodes() {
        return new PackageBarcodes();
    }

    /**
     * Create an instance of {@link InternationalControlledExportDetail }
     * 
     */
    public InternationalControlledExportDetail createInternationalControlledExportDetail() {
        return new InternationalControlledExportDetail();
    }

    /**
     * Create an instance of {@link ShippingDocument }
     * 
     */
    public ShippingDocument createShippingDocument() {
        return new ShippingDocument();
    }

    /**
     * Create an instance of {@link FreightRateDetail }
     * 
     */
    public FreightRateDetail createFreightRateDetail() {
        return new FreightRateDetail();
    }

    /**
     * Create an instance of {@link CompletedCodDetail }
     * 
     */
    public CompletedCodDetail createCompletedCodDetail() {
        return new CompletedCodDetail();
    }

    /**
     * Create an instance of {@link Payment }
     * 
     */
    public Payment createPayment() {
        return new Payment();
    }

    /**
     * Create an instance of {@link DocumentFormatOptionsRequested }
     * 
     */
    public DocumentFormatOptionsRequested createDocumentFormatOptionsRequested() {
        return new DocumentFormatOptionsRequested();
    }

    /**
     * Create an instance of {@link TransactionDetail }
     * 
     */
    public TransactionDetail createTransactionDetail() {
        return new TransactionDetail();
    }

    /**
     * Create an instance of {@link ExportDeclarationDetail }
     * 
     */
    public ExportDeclarationDetail createExportDeclarationDetail() {
        return new ExportDeclarationDetail();
    }

    /**
     * Create an instance of {@link ShippingDocumentPrintDetail }
     * 
     */
    public ShippingDocumentPrintDetail createShippingDocumentPrintDetail() {
        return new ShippingDocumentPrintDetail();
    }

    /**
     * Create an instance of {@link ShipmentLegRateDetail }
     * 
     */
    public ShipmentLegRateDetail createShipmentLegRateDetail() {
        return new ShipmentLegRateDetail();
    }

    /**
     * Create an instance of {@link CommercialInvoice }
     * 
     */
    public CommercialInvoice createCommercialInvoice() {
        return new CommercialInvoice();
    }

    /**
     * Create an instance of {@link ShippingDocumentPart }
     * 
     */
    public ShippingDocumentPart createShippingDocumentPart() {
        return new ShippingDocumentPart();
    }

    /**
     * Create an instance of {@link Dimensions }
     * 
     */
    public Dimensions createDimensions() {
        return new Dimensions();
    }

    /**
     * Create an instance of {@link EMailNotificationDetail }
     * 
     */
    public EMailNotificationDetail createEMailNotificationDetail() {
        return new EMailNotificationDetail();
    }

    /**
     * Create an instance of {@link LicenseOrPermitDetail }
     * 
     */
    public LicenseOrPermitDetail createLicenseOrPermitDetail() {
        return new LicenseOrPermitDetail();
    }

    /**
     * Create an instance of {@link LabelSpecification }
     * 
     */
    public LabelSpecification createLabelSpecification() {
        return new LabelSpecification();
    }

    /**
     * Create an instance of {@link RateDiscount }
     * 
     */
    public RateDiscount createRateDiscount() {
        return new RateDiscount();
    }

    /**
     * Create an instance of {@link ShipmentSpecialServicesRequested }
     * 
     */
    public ShipmentSpecialServicesRequested createShipmentSpecialServicesRequested() {
        return new ShipmentSpecialServicesRequested();
    }

    /**
     * Create an instance of {@link CompletedShipmentDetail }
     * 
     */
    public CompletedShipmentDetail createCompletedShipmentDetail() {
        return new CompletedShipmentDetail();
    }

    /**
     * Create an instance of {@link ShippingDocumentEMailRecipient }
     * 
     */
    public ShippingDocumentEMailRecipient createShippingDocumentEMailRecipient() {
        return new ShippingDocumentEMailRecipient();
    }

    /**
     * Create an instance of {@link ShipmentManifestDetail }
     * 
     */
    public ShipmentManifestDetail createShipmentManifestDetail() {
        return new ShipmentManifestDetail();
    }

    /**
     * Create an instance of {@link EdtTaxDetail }
     * 
     */
    public EdtTaxDetail createEdtTaxDetail() {
        return new EdtTaxDetail();
    }

    /**
     * Create an instance of {@link CompletedHazardousShipmentDetail }
     * 
     */
    public CompletedHazardousShipmentDetail createCompletedHazardousShipmentDetail() {
        return new CompletedHazardousShipmentDetail();
    }

    /**
     * Create an instance of {@link CodDetail }
     * 
     */
    public CodDetail createCodDetail() {
        return new CodDetail();
    }

    /**
     * Create an instance of {@link CustomerReference }
     * 
     */
    public CustomerReference createCustomerReference() {
        return new CustomerReference();
    }

    /**
     * Create an instance of {@link Money }
     * 
     */
    public Money createMoney() {
        return new Money();
    }

    /**
     * Create an instance of {@link PrintedReference }
     * 
     */
    public PrintedReference createPrintedReference() {
        return new PrintedReference();
    }

    /**
     * Create an instance of {@link Volume }
     * 
     */
    public Volume createVolume() {
        return new Volume();
    }

    /**
     * Create an instance of {@link ShippingDocumentFormat }
     * 
     */
    public ShippingDocumentFormat createShippingDocumentFormat() {
        return new ShippingDocumentFormat();
    }

    /**
     * Create an instance of {@link Localization }
     * 
     */
    public Localization createLocalization() {
        return new Localization();
    }

    /**
     * Create an instance of {@link CustomDeliveryWindowDetail }
     * 
     */
    public CustomDeliveryWindowDetail createCustomDeliveryWindowDetail() {
        return new CustomDeliveryWindowDetail();
    }

    /**
     * Create an instance of {@link HazardousCommodityPackingDetail }
     * 
     */
    public HazardousCommodityPackingDetail createHazardousCommodityPackingDetail() {
        return new HazardousCommodityPackingDetail();
    }

    /**
     * Create an instance of {@link ContentRecord }
     * 
     */
    public ContentRecord createContentRecord() {
        return new ContentRecord();
    }

    /**
     * Create an instance of {@link FreightGuaranteeDetail }
     * 
     */
    public FreightGuaranteeDetail createFreightGuaranteeDetail() {
        return new FreightGuaranteeDetail();
    }

    /**
     * Create an instance of {@link DangerousGoodsContainer }
     * 
     */
    public DangerousGoodsContainer createDangerousGoodsContainer() {
        return new DangerousGoodsContainer();
    }

    /**
     * Create an instance of {@link VariableHandlingCharges }
     * 
     */
    public VariableHandlingCharges createVariableHandlingCharges() {
        return new VariableHandlingCharges();
    }

    /**
     * Create an instance of {@link ReturnEMailDetail }
     * 
     */
    public ReturnEMailDetail createReturnEMailDetail() {
        return new ReturnEMailDetail();
    }

    /**
     * Create an instance of {@link InternationalTrafficInArmsRegulationsDetail }
     * 
     */
    public InternationalTrafficInArmsRegulationsDetail createInternationalTrafficInArmsRegulationsDetail() {
        return new InternationalTrafficInArmsRegulationsDetail();
    }

    /**
     * Create an instance of {@link PackageRating }
     * 
     */
    public PackageRating createPackageRating() {
        return new PackageRating();
    }

    /**
     * Create an instance of {@link TrackingId }
     * 
     */
    public TrackingId createTrackingId() {
        return new TrackingId();
    }

    /**
     * Create an instance of {@link AlcoholDetail }
     * 
     */
    public AlcoholDetail createAlcoholDetail() {
        return new AlcoholDetail();
    }

    /**
     * Create an instance of {@link CustomLabelGraphicEntry }
     * 
     */
    public CustomLabelGraphicEntry createCustomLabelGraphicEntry() {
        return new CustomLabelGraphicEntry();
    }

    /**
     * Create an instance of {@link PackageSpecialServicesRequested }
     * 
     */
    public PackageSpecialServicesRequested createPackageSpecialServicesRequested() {
        return new PackageSpecialServicesRequested();
    }

    /**
     * Create an instance of {@link PendingShipmentAccessorDetail }
     * 
     */
    public PendingShipmentAccessorDetail createPendingShipmentAccessorDetail() {
        return new PendingShipmentAccessorDetail();
    }

    /**
     * Create an instance of {@link EtdDetail }
     * 
     */
    public EtdDetail createEtdDetail() {
        return new EtdDetail();
    }

    /**
     * Create an instance of {@link FreightShipmentDetail }
     * 
     */
    public FreightShipmentDetail createFreightShipmentDetail() {
        return new FreightShipmentDetail();
    }

    /**
     * Create an instance of {@link RadionuclideDetail }
     * 
     */
    public RadionuclideDetail createRadionuclideDetail() {
        return new RadionuclideDetail();
    }

    /**
     * Create an instance of {@link ShipmentRateDetail }
     * 
     */
    public ShipmentRateDetail createShipmentRateDetail() {
        return new ShipmentRateDetail();
    }

    /**
     * Create an instance of {@link CustomsClearanceDetail }
     * 
     */
    public CustomsClearanceDetail createCustomsClearanceDetail() {
        return new CustomsClearanceDetail();
    }

    /**
     * Create an instance of {@link ClientDetail }
     * 
     */
    public ClientDetail createClientDetail() {
        return new ClientDetail();
    }

    /**
     * Create an instance of {@link AdditionalLabelsDetail }
     * 
     */
    public AdditionalLabelsDetail createAdditionalLabelsDetail() {
        return new AdditionalLabelsDetail();
    }

    /**
     * Create an instance of {@link CustomLabelPosition }
     * 
     */
    public CustomLabelPosition createCustomLabelPosition() {
        return new CustomLabelPosition();
    }

    /**
     * Create an instance of {@link ValidatedHazardousContainer }
     * 
     */
    public ValidatedHazardousContainer createValidatedHazardousContainer() {
        return new ValidatedHazardousContainer();
    }

    /**
     * Create an instance of {@link CompletedTagDetail }
     * 
     */
    public CompletedTagDetail createCompletedTagDetail() {
        return new CompletedTagDetail();
    }

    /**
     * Create an instance of {@link FreightRateNotation }
     * 
     */
    public FreightRateNotation createFreightRateNotation() {
        return new FreightRateNotation();
    }

    /**
     * Create an instance of {@link ValidatedHazardousCommodityDescription }
     * 
     */
    public ValidatedHazardousCommodityDescription createValidatedHazardousCommodityDescription() {
        return new ValidatedHazardousCommodityDescription();
    }

    /**
     * Create an instance of {@link ShipmentDryIceDetail }
     * 
     */
    public ShipmentDryIceDetail createShipmentDryIceDetail() {
        return new ShipmentDryIceDetail();
    }

    /**
     * Create an instance of {@link RegulatoryLabelContentDetail }
     * 
     */
    public RegulatoryLabelContentDetail createRegulatoryLabelContentDetail() {
        return new RegulatoryLabelContentDetail();
    }

    /**
     * Create an instance of {@link AssociatedShipmentDetail }
     * 
     */
    public AssociatedShipmentDetail createAssociatedShipmentDetail() {
        return new AssociatedShipmentDetail();
    }

    /**
     * Create an instance of {@link HazardousCommodityPackagingDetail }
     * 
     */
    public HazardousCommodityPackagingDetail createHazardousCommodityPackagingDetail() {
        return new HazardousCommodityPackagingDetail();
    }

    /**
     * Create an instance of {@link FreightBaseCharge }
     * 
     */
    public FreightBaseCharge createFreightBaseCharge() {
        return new FreightBaseCharge();
    }

    /**
     * Create an instance of {@link RadionuclideActivity }
     * 
     */
    public RadionuclideActivity createRadionuclideActivity() {
        return new RadionuclideActivity();
    }

    /**
     * Create an instance of {@link PickupDetail }
     * 
     */
    public PickupDetail createPickupDetail() {
        return new PickupDetail();
    }

    /**
     * Create an instance of {@link ConfigurableLabelReferenceEntry }
     * 
     */
    public ConfigurableLabelReferenceEntry createConfigurableLabelReferenceEntry() {
        return new ConfigurableLabelReferenceEntry();
    }

    /**
     * Create an instance of {@link CompletedHazardousSummaryDetail }
     * 
     */
    public CompletedHazardousSummaryDetail createCompletedHazardousSummaryDetail() {
        return new CompletedHazardousSummaryDetail();
    }

    /**
     * Create an instance of {@link RadioactivityDetail }
     * 
     */
    public RadioactivityDetail createRadioactivityDetail() {
        return new RadioactivityDetail();
    }

    /**
     * Create an instance of {@link EMailRecipient }
     * 
     */
    public EMailRecipient createEMailRecipient() {
        return new EMailRecipient();
    }

    /**
     * Create an instance of {@link CurrencyExchangeRate }
     * 
     */
    public CurrencyExchangeRate createCurrencyExchangeRate() {
        return new CurrencyExchangeRate();
    }

    /**
     * Create an instance of {@link WebAuthenticationCredential }
     * 
     */
    public WebAuthenticationCredential createWebAuthenticationCredential() {
        return new WebAuthenticationCredential();
    }

    /**
     * Create an instance of {@link Rebate }
     * 
     */
    public Rebate createRebate() {
        return new Rebate();
    }

    /**
     * Create an instance of {@link CompletedSmartPostDetail }
     * 
     */
    public CompletedSmartPostDetail createCompletedSmartPostDetail() {
        return new CompletedSmartPostDetail();
    }

    /**
     * Create an instance of {@link CustomLabelBoxEntry }
     * 
     */
    public CustomLabelBoxEntry createCustomLabelBoxEntry() {
        return new CustomLabelBoxEntry();
    }

    /**
     * Create an instance of {@link NaftaProducer }
     * 
     */
    public NaftaProducer createNaftaProducer() {
        return new NaftaProducer();
    }

    /**
     * Create an instance of {@link DeliveryOnInvoiceAcceptanceDetail }
     * 
     */
    public DeliveryOnInvoiceAcceptanceDetail createDeliveryOnInvoiceAcceptanceDetail() {
        return new DeliveryOnInvoiceAcceptanceDetail();
    }

    /**
     * Create an instance of {@link PackageRateDetail }
     * 
     */
    public PackageRateDetail createPackageRateDetail() {
        return new PackageRateDetail();
    }

    /**
     * Create an instance of {@link ContactAndAddress }
     * 
     */
    public ContactAndAddress createContactAndAddress() {
        return new ContactAndAddress();
    }

    /**
     * Create an instance of {@link WebAuthenticationDetail }
     * 
     */
    public WebAuthenticationDetail createWebAuthenticationDetail() {
        return new WebAuthenticationDetail();
    }

    /**
     * Create an instance of {@link RequestedShipment }
     * 
     */
    public RequestedShipment createRequestedShipment() {
        return new RequestedShipment();
    }

    /**
     * Create an instance of {@link HazardousCommodityQuantityDetail }
     * 
     */
    public HazardousCommodityQuantityDetail createHazardousCommodityQuantityDetail() {
        return new HazardousCommodityQuantityDetail();
    }

    /**
     * Create an instance of {@link ValidatedHazardousCommodityContent }
     * 
     */
    public ValidatedHazardousCommodityContent createValidatedHazardousCommodityContent() {
        return new ValidatedHazardousCommodityContent();
    }

    /**
     * Create an instance of {@link StringBarcode }
     * 
     */
    public StringBarcode createStringBarcode() {
        return new StringBarcode();
    }

    /**
     * Create an instance of {@link GeneralAgencyAgreementDetail }
     * 
     */
    public GeneralAgencyAgreementDetail createGeneralAgencyAgreementDetail() {
        return new GeneralAgencyAgreementDetail();
    }

    /**
     * Create an instance of {@link Commodity }
     * 
     */
    public Commodity createCommodity() {
        return new Commodity();
    }

    /**
     * Create an instance of {@link EMailNotificationRecipient }
     * 
     */
    public EMailNotificationRecipient createEMailNotificationRecipient() {
        return new EMailNotificationRecipient();
    }

    /**
     * Create an instance of {@link FreightSpecialServicePayment }
     * 
     */
    public FreightSpecialServicePayment createFreightSpecialServicePayment() {
        return new FreightSpecialServicePayment();
    }

    /**
     * Create an instance of {@link CustomLabelTextBoxEntry }
     * 
     */
    public CustomLabelTextBoxEntry createCustomLabelTextBoxEntry() {
        return new CustomLabelTextBoxEntry();
    }

    /**
     * Create an instance of {@link CodAddTransportationChargesDetail }
     * 
     */
    public CodAddTransportationChargesDetail createCodAddTransportationChargesDetail() {
        return new CodAddTransportationChargesDetail();
    }

    /**
     * Create an instance of {@link Weight }
     * 
     */
    public Weight createWeight() {
        return new Weight();
    }

    /**
     * Create an instance of {@link CustomerSpecifiedLabelDetail }
     * 
     */
    public CustomerSpecifiedLabelDetail createCustomerSpecifiedLabelDetail() {
        return new CustomerSpecifiedLabelDetail();
    }

    /**
     * Create an instance of {@link ShippingDocumentDispositionDetail }
     * 
     */
    public ShippingDocumentDispositionDetail createShippingDocumentDispositionDetail() {
        return new ShippingDocumentDispositionDetail();
    }

    /**
     * Create an instance of {@link VersionId }
     * 
     */
    public VersionId createVersionId() {
        return new VersionId();
    }

    /**
     * Create an instance of {@link HazardousCommodityContent }
     * 
     */
    public HazardousCommodityContent createHazardousCommodityContent() {
        return new HazardousCommodityContent();
    }

    /**
     * Create an instance of {@link CompletedEtdDetail }
     * 
     */
    public CompletedEtdDetail createCompletedEtdDetail() {
        return new CompletedEtdDetail();
    }

    /**
     * Create an instance of {@link Party }
     * 
     */
    public Party createParty() {
        return new Party();
    }

    /**
     * Create an instance of {@link CertificateOfOriginDetail }
     * 
     */
    public CertificateOfOriginDetail createCertificateOfOriginDetail() {
        return new CertificateOfOriginDetail();
    }

    /**
     * Create an instance of {@link ShipmentConfigurationData }
     * 
     */
    public ShipmentConfigurationData createShipmentConfigurationData() {
        return new ShipmentConfigurationData();
    }

    /**
     * Create an instance of {@link DocTabZoneSpecification }
     * 
     */
    public DocTabZoneSpecification createDocTabZoneSpecification() {
        return new DocTabZoneSpecification();
    }

    /**
     * Create an instance of {@link Notification }
     * 
     */
    public Notification createNotification() {
        return new Notification();
    }

    /**
     * Create an instance of {@link ShippingDocumentEMailDetail }
     * 
     */
    public ShippingDocumentEMailDetail createShippingDocumentEMailDetail() {
        return new ShippingDocumentEMailDetail();
    }

    /**
     * Create an instance of {@link CustomsDeclarationStatementDetail }
     * 
     */
    public CustomsDeclarationStatementDetail createCustomsDeclarationStatementDetail() {
        return new CustomsDeclarationStatementDetail();
    }

    /**
     * Create an instance of {@link HazardousCommodityOptionDetail }
     * 
     */
    public HazardousCommodityOptionDetail createHazardousCommodityOptionDetail() {
        return new HazardousCommodityOptionDetail();
    }

    /**
     * Create an instance of {@link ExpressFreightDetail }
     * 
     */
    public ExpressFreightDetail createExpressFreightDetail() {
        return new ExpressFreightDetail();
    }

    /**
     * Create an instance of {@link OperationalInstruction }
     * 
     */
    public OperationalInstruction createOperationalInstruction() {
        return new OperationalInstruction();
    }

    /**
     * Create an instance of {@link DangerousGoodsSignatory }
     * 
     */
    public DangerousGoodsSignatory createDangerousGoodsSignatory() {
        return new DangerousGoodsSignatory();
    }

    /**
     * Create an instance of {@link UploadDocumentReferenceDetail }
     * 
     */
    public UploadDocumentReferenceDetail createUploadDocumentReferenceDetail() {
        return new UploadDocumentReferenceDetail();
    }

    /**
     * Create an instance of {@link ShippingDocumentSpecification }
     * 
     */
    public ShippingDocumentSpecification createShippingDocumentSpecification() {
        return new ShippingDocumentSpecification();
    }

    /**
     * Create an instance of {@link ReturnShipmentDetail }
     * 
     */
    public ReturnShipmentDetail createReturnShipmentDetail() {
        return new ReturnShipmentDetail();
    }

    /**
     * Create an instance of {@link NaftaCertificateOfOriginDetail }
     * 
     */
    public NaftaCertificateOfOriginDetail createNaftaCertificateOfOriginDetail() {
        return new NaftaCertificateOfOriginDetail();
    }

    /**
     * Create an instance of {@link VariableHandlingChargeDetail }
     * 
     */
    public VariableHandlingChargeDetail createVariableHandlingChargeDetail() {
        return new VariableHandlingChargeDetail();
    }

    /**
     * Create an instance of {@link CustomDocumentDetail }
     * 
     */
    public CustomDocumentDetail createCustomDocumentDetail() {
        return new CustomDocumentDetail();
    }

    /**
     * Create an instance of {@link BinaryBarcode }
     * 
     */
    public BinaryBarcode createBinaryBarcode() {
        return new BinaryBarcode();
    }

    /**
     * Create an instance of {@link SmartPostShipmentProcessingOptionsRequested }
     * 
     */
    public SmartPostShipmentProcessingOptionsRequested createSmartPostShipmentProcessingOptionsRequested() {
        return new SmartPostShipmentProcessingOptionsRequested();
    }

    /**
     * Create an instance of {@link PackageOperationalDetail }
     * 
     */
    public PackageOperationalDetail createPackageOperationalDetail() {
        return new PackageOperationalDetail();
    }

    /**
     * Create an instance of {@link ShipmentRating }
     * 
     */
    public ShipmentRating createShipmentRating() {
        return new ShipmentRating();
    }

    /**
     * Create an instance of {@link Tax }
     * 
     */
    public Tax createTax() {
        return new Tax();
    }

    /**
     * Create an instance of {@link HazardousCommodityDescription }
     * 
     */
    public HazardousCommodityDescription createHazardousCommodityDescription() {
        return new HazardousCommodityDescription();
    }

    /**
     * Create an instance of {@link CustomLabelTextEntry }
     * 
     */
    public CustomLabelTextEntry createCustomLabelTextEntry() {
        return new CustomLabelTextEntry();
    }

    /**
     * Create an instance of {@link NaftaLowValueStatementDetail }
     * 
     */
    public NaftaLowValueStatementDetail createNaftaLowValueStatementDetail() {
        return new NaftaLowValueStatementDetail();
    }

    /**
     * Create an instance of {@link CommercialInvoiceDetail }
     * 
     */
    public CommercialInvoiceDetail createCommercialInvoiceDetail() {
        return new CommercialInvoiceDetail();
    }

    /**
     * Create an instance of {@link PendingShipmentProcessingOptionsRequested }
     * 
     */
    public PendingShipmentProcessingOptionsRequested createPendingShipmentProcessingOptionsRequested() {
        return new PendingShipmentProcessingOptionsRequested();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link EdtExciseCondition }
     * 
     */
    public EdtExciseCondition createEdtExciseCondition() {
        return new EdtExciseCondition();
    }

    /**
     * Create an instance of {@link FreightAddressLabelDetail }
     * 
     */
    public FreightAddressLabelDetail createFreightAddressLabelDetail() {
        return new FreightAddressLabelDetail();
    }

    /**
     * Create an instance of {@link Measure }
     * 
     */
    public Measure createMeasure() {
        return new Measure();
    }

    /**
     * Create an instance of {@link PriorityAlertDetail }
     * 
     */
    public PriorityAlertDetail createPriorityAlertDetail() {
        return new PriorityAlertDetail();
    }

    /**
     * Create an instance of {@link EdtCommodityTax }
     * 
     */
    public EdtCommodityTax createEdtCommodityTax() {
        return new EdtCommodityTax();
    }

    /**
     * Create an instance of {@link AdrLicenseDetail }
     * 
     */
    public AdrLicenseDetail createAdrLicenseDetail() {
        return new AdrLicenseDetail();
    }

    /**
     * Create an instance of {@link CompletedHoldAtLocationDetail }
     * 
     */
    public CompletedHoldAtLocationDetail createCompletedHoldAtLocationDetail() {
        return new CompletedHoldAtLocationDetail();
    }

    /**
     * Create an instance of {@link BrokerDetail }
     * 
     */
    public BrokerDetail createBrokerDetail() {
        return new BrokerDetail();
    }

    /**
     * Create an instance of {@link LiabilityCoverageDetail }
     * 
     */
    public LiabilityCoverageDetail createLiabilityCoverageDetail() {
        return new LiabilityCoverageDetail();
    }

    /**
     * Create an instance of {@link HoldAtLocationDetail }
     * 
     */
    public HoldAtLocationDetail createHoldAtLocationDetail() {
        return new HoldAtLocationDetail();
    }

    /**
     * Create an instance of {@link HomeDeliveryPremiumDetail }
     * 
     */
    public HomeDeliveryPremiumDetail createHomeDeliveryPremiumDetail() {
        return new HomeDeliveryPremiumDetail();
    }

    /**
     * Create an instance of {@link DateRange }
     * 
     */
    public DateRange createDateRange() {
        return new DateRange();
    }

    /**
     * Create an instance of {@link CustomLabelBarcodeEntry }
     * 
     */
    public CustomLabelBarcodeEntry createCustomLabelBarcodeEntry() {
        return new CustomLabelBarcodeEntry();
    }

    /**
     * Create an instance of {@link ReturnAssociationDetail }
     * 
     */
    public ReturnAssociationDetail createReturnAssociationDetail() {
        return new ReturnAssociationDetail();
    }

    /**
     * Create an instance of {@link NetExplosiveDetail }
     * 
     */
    public NetExplosiveDetail createNetExplosiveDetail() {
        return new NetExplosiveDetail();
    }

    /**
     * Create an instance of {@link DocTabContentZone001 }
     * 
     */
    public DocTabContentZone001 createDocTabContentZone001() {
        return new DocTabContentZone001();
    }

    /**
     * Create an instance of {@link EMailLabelDetail }
     * 
     */
    public EMailLabelDetail createEMailLabelDetail() {
        return new EMailLabelDetail();
    }

    /**
     * Create an instance of {@link DangerousGoodsDetail }
     * 
     */
    public DangerousGoodsDetail createDangerousGoodsDetail() {
        return new DangerousGoodsDetail();
    }

    /**
     * Create an instance of {@link RecommendedDocumentSpecification }
     * 
     */
    public RecommendedDocumentSpecification createRecommendedDocumentSpecification() {
        return new RecommendedDocumentSpecification();
    }

    /**
     * Create an instance of {@link FreightShipmentLineItem }
     * 
     */
    public FreightShipmentLineItem createFreightShipmentLineItem() {
        return new FreightShipmentLineItem();
    }

    /**
     * Create an instance of {@link CompletedHazardousPackageDetail }
     * 
     */
    public CompletedHazardousPackageDetail createCompletedHazardousPackageDetail() {
        return new CompletedHazardousPackageDetail();
    }

    /**
     * Create an instance of {@link ExportDetail }
     * 
     */
    public ExportDetail createExportDetail() {
        return new ExportDetail();
    }

    /**
     * Create an instance of {@link Payor }
     * 
     */
    public Payor createPayor() {
        return new Payor();
    }

    /**
     * Create an instance of {@link DocTabContentBarcoded }
     * 
     */
    public DocTabContentBarcoded createDocTabContentBarcoded() {
        return new DocTabContentBarcoded();
    }

    /**
     * Create an instance of {@link SignatureOptionDetail }
     * 
     */
    public SignatureOptionDetail createSignatureOptionDetail() {
        return new SignatureOptionDetail();
    }

    /**
     * Create an instance of {@link TaxpayerIdentification }
     * 
     */
    public TaxpayerIdentification createTaxpayerIdentification() {
        return new TaxpayerIdentification();
    }

    /**
     * Create an instance of {@link RequestedPackageLineItem }
     * 
     */
    public RequestedPackageLineItem createRequestedPackageLineItem() {
        return new RequestedPackageLineItem();
    }

    /**
     * Create an instance of {@link LinearMeasure }
     * 
     */
    public LinearMeasure createLinearMeasure() {
        return new LinearMeasure();
    }

    /**
     * Create an instance of {@link Surcharge }
     * 
     */
    public Surcharge createSurcharge() {
        return new Surcharge();
    }

    /**
     * Create an instance of {@link DangerousGoodsShippersDeclarationDetail }
     * 
     */
    public DangerousGoodsShippersDeclarationDetail createDangerousGoodsShippersDeclarationDetail() {
        return new DangerousGoodsShippersDeclarationDetail();
    }

    /**
     * Create an instance of {@link PendingShipmentAccessDetail }
     * 
     */
    public PendingShipmentAccessDetail createPendingShipmentAccessDetail() {
        return new PendingShipmentAccessDetail();
    }

    /**
     * Create an instance of {@link SmartPostShipmentDetail }
     * 
     */
    public SmartPostShipmentDetail createSmartPostShipmentDetail() {
        return new SmartPostShipmentDetail();
    }

    /**
     * Create an instance of {@link Op900Detail }
     * 
     */
    public Op900Detail createOp900Detail() {
        return new Op900Detail();
    }

    /**
     * Create an instance of {@link RecipientCustomsId }
     * 
     */
    public RecipientCustomsId createRecipientCustomsId() {
        return new RecipientCustomsId();
    }

    /**
     * Create an instance of {@link CustomerImageUsage }
     * 
     */
    public CustomerImageUsage createCustomerImageUsage() {
        return new CustomerImageUsage();
    }

    /**
     * Create an instance of {@link ShipmentOperationalDetail }
     * 
     */
    public ShipmentOperationalDetail createShipmentOperationalDetail() {
        return new ShipmentOperationalDetail();
    }

    /**
     * Create an instance of {@link PendingShipmentDetail }
     * 
     */
    public PendingShipmentDetail createPendingShipmentDetail() {
        return new PendingShipmentDetail();
    }

    /**
     * Create an instance of {@link CompletedPackageDetail }
     * 
     */
    public CompletedPackageDetail createCompletedPackageDetail() {
        return new CompletedPackageDetail();
    }

    /**
     * Create an instance of {@link Rma }
     * 
     */
    public Rma createRma() {
        return new Rma();
    }

    /**
     * Create an instance of {@link CustomsOptionDetail }
     * 
     */
    public CustomsOptionDetail createCustomsOptionDetail() {
        return new CustomsOptionDetail();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link HazardousCommodityInnerReceptacleDetail }
     * 
     */
    public HazardousCommodityInnerReceptacleDetail createHazardousCommodityInnerReceptacleDetail() {
        return new HazardousCommodityInnerReceptacleDetail();
    }

    /**
     * Create an instance of {@link EmailOptionsRequested }
     * 
     */
    public EmailOptionsRequested createEmailOptionsRequested() {
        return new EmailOptionsRequested();
    }

    /**
     * Create an instance of {@link ReturnInstructionsDetail }
     * 
     */
    public ReturnInstructionsDetail createReturnInstructionsDetail() {
        return new ReturnInstructionsDetail();
    }

    /**
     * Create an instance of {@link CodReturnPackageDetail }
     * 
     */
    public CodReturnPackageDetail createCodReturnPackageDetail() {
        return new CodReturnPackageDetail();
    }

    /**
     * Create an instance of {@link NaftaCommodityDetail }
     * 
     */
    public NaftaCommodityDetail createNaftaCommodityDetail() {
        return new NaftaCommodityDetail();
    }

    /**
     * Create an instance of {@link DocTabContent }
     * 
     */
    public DocTabContent createDocTabContent() {
        return new DocTabContent();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessShipmentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ProcessShipmentRequest")
    public JAXBElement<ProcessShipmentRequest> createProcessShipmentRequest(ProcessShipmentRequest value) {
        return new JAXBElement<ProcessShipmentRequest>(_ProcessShipmentRequest_QNAME, ProcessShipmentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteTagRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "DeleteTagRequest")
    public JAXBElement<DeleteTagRequest> createDeleteTagRequest(DeleteTagRequest value) {
        return new JAXBElement<DeleteTagRequest>(_DeleteTagRequest_QNAME, DeleteTagRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessTagRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ProcessTagRequest")
    public JAXBElement<ProcessTagRequest> createProcessTagRequest(ProcessTagRequest value) {
        return new JAXBElement<ProcessTagRequest>(_ProcessTagRequest_QNAME, ProcessTagRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessTagReply }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ProcessTagReply")
    public JAXBElement<ProcessTagReply> createProcessTagReply(ProcessTagReply value) {
        return new JAXBElement<ProcessTagReply>(_ProcessTagReply_QNAME, ProcessTagReply.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShipmentReply }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ShipmentReply")
    public JAXBElement<ShipmentReply> createShipmentReply(ShipmentReply value) {
        return new JAXBElement<ShipmentReply>(_ShipmentReply_QNAME, ShipmentReply.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteShipmentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "DeleteShipmentRequest")
    public JAXBElement<DeleteShipmentRequest> createDeleteShipmentRequest(DeleteShipmentRequest value) {
        return new JAXBElement<DeleteShipmentRequest>(_DeleteShipmentRequest_QNAME, DeleteShipmentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidateShipmentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ValidateShipmentRequest")
    public JAXBElement<ValidateShipmentRequest> createValidateShipmentRequest(ValidateShipmentRequest value) {
        return new JAXBElement<ValidateShipmentRequest>(_ValidateShipmentRequest_QNAME, ValidateShipmentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessShipmentReply }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fedex.com/ws/ship/v17", name = "ProcessShipmentReply")
    public JAXBElement<ProcessShipmentReply> createProcessShipmentReply(ProcessShipmentReply value) {
        return new JAXBElement<ProcessShipmentReply>(_ProcessShipmentReply_QNAME, ProcessShipmentReply.class, null, value);
    }

}
