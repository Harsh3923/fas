import React, { useState, useEffect } from "react";
import Layout from "../component/Layout";
import ApiService from "../service/ApiService";
import { useNavigate, useParams } from "react-router-dom";

const AddEditProductPage = () => {
  const { productId } = useParams("");
  const [name, setName] = useState("");
  const [sku, setSku] = useState("");
  const [price, setPrice] = useState("");
  const [stockQuantity, setStockQuantity] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [description, setDescription] = useState("");
  const [supplierId, setSupplierId] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [imageUrl, setImageUrl] = useState("");
  const [isEditing, setIsEditing] = useState(false);

  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);  // ✅ ADDED

  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {

    const fetchCategories = async () => {
      try {
        const categoriesData = await ApiService.getAllCategory();
        setCategories(categoriesData.categories);
      } catch (error) {
        showMessage(error.response?.data?.message || "Error Getting Categories");
      }
    };

    const fetchSuppliers = async () => {
      try {
        const suppliersData = await ApiService.getAllSuppliers();
        setSuppliers(suppliersData.suppliers);
      } catch (error) {
        showMessage(error.response?.data?.message || "Error fetching suppliers");
      }
    };


    const fetchProductById = async () => {
      if (productId) {
        setIsEditing(true);
        try {
          const productData = await ApiService.getProductById(productId);
          if (productData.status === 200) {
            setName(productData.product.name);
            setSku(productData.product.sku);
            setPrice(productData.product.price);
            setStockQuantity(productData.product.stockQuantity);
            setCategoryId(productData.product.categoryId);
            setDescription(productData.product.description);
            setSupplierId(productData.product.supplierId);
            setImageUrl(productData.product.imageUrl);
          }
        } catch (error) {
          showMessage("Error Getting Product By Id");
        }
      }
    };

    fetchCategories();
    fetchSuppliers();   // ✅ ADDED
    if (productId) fetchProductById();

  }, [productId]);

  const showMessage = (msg) => {
    setMessage(msg);
    setTimeout(() => setMessage(""), 4000);
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    setImageFile(file);
    const reader = new FileReader();
    reader.onloadend = () => setImageUrl(reader.result);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("name", name);
    formData.append("sku", sku);
    formData.append("price", parseFloat(price));
    formData.append("stockQuantity", parseFloat(stockQuantity));
    formData.append("categoryId", categoryId);
    formData.append("supplierId", supplierId);   // ✅ FIXED key
    formData.append("description", description);

    if (imageFile) {
      formData.append("imageFile", imageFile);
    }

    try {
      if (isEditing) {
        formData.append("productId", productId);
        await ApiService.updateProduct(formData);
        showMessage("Product Updated Successfully");
      } else {
        await ApiService.addProduct(formData);
        showMessage("Product Added Successfully");
      }
      navigate("/product");
    } catch (error) {
      showMessage("Error Saving Product");
    }
  };

  return (
    <Layout>
      {message && <div className="message">{message}</div>}

      <div className="product-form-page">
        <h1>{isEditing ? "Edit Product" : "Add Product"}</h1>

        <form onSubmit={handleSubmit}>

          {/* Name */}
          <div className="form-group">
            <label>Product Name</label>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>

          {/* SKU */}
          <div className="form-group">
            <label>SKU</label>
            <input type="text" value={sku} onChange={(e) => setSku(e.target.value)} required />
          </div>

          {/* Stock */}
          <div className="form-group">
            <label>Stock Quantity</label>
            <input type="number" value={stockQuantity} onChange={(e) => setStockQuantity(e.target.value)} required />
          </div>

          {/* Price */}
          <div className="form-group">
            <label>Price</label>
            <input type="number" value={price} onChange={(e) => setPrice(e.target.value)} required />
          </div>

          {/* Description */}
          <div className="form-group">
            <label>Description</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>

          {/* Category */}
          <div className="form-group">
            <label>Category</label>
            <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required>
              <option value="">Select a Category</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
          </div>

          {/* Supplier */}
          <div className="form-group">
            <label>Supplier</label>
            <select value={supplierId} onChange={(e) => setSupplierId(e.target.value)} required>
              <option value="">Select a Supplier</option>
              {suppliers.map((sup) => (
                <option key={sup.id} value={sup.id}>
                  {sup.name}
                </option>
              ))}
            </select>
          </div>

          {/* Image */}
          <div className="form-group">
            <label>Product Image</label>
            <input type="file" onChange={handleImageChange} />
            {imageUrl && <img src={imageUrl} alt="preview" className="image-preview" />}
          </div>

          <button type="submit">
            {isEditing ? "Edit Product" : "Add Product"}
          </button>

        </form>
      </div>
    </Layout>
  );
};

export default AddEditProductPage;
